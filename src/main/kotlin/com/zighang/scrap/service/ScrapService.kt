package com.zighang.scrap.service

import com.zighang.core.application.ObjectStorageService
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.memo.entity.Memo
import com.zighang.memo.repository.MemoRepository
import com.zighang.jobposting.infrastructure.mapper.CompanyMapper
import com.zighang.scrap.dto.request.UpsertScrapRequest
import com.zighang.scrap.entity.Scrap
import com.zighang.jobposting.service.JobPostingAnalysisService
import com.zighang.scrap.dto.response.*
import com.zighang.scrap.repository.ScrapRepository
import com.zighang.scrap.value.FileType
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.multipart.MultipartFile

@Service
@Slf4j
class ScrapService(
    private val scrapRepository: ScrapRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val memoRepository: MemoRepository,
    private val objectStorageService: ObjectStorageService,
    private val jobPostingAnalysisService: JobPostingAnalysisService,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val personalityAnalysisService: PersonalityAnalysisService,
    private val companyMapper: CompanyMapper,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun upsert(customUserDetails: CustomUserDetails, upsertScrapRequest: UpsertScrapRequest) {
        val jobPosting = jobPostingRepository.findById(upsertScrapRequest.jobPostingId)
            .orElseThrow{DomainException(GlobalErrorCode.NOT_EXIST_JOB_POSTING)}

        // 우대사항 / 자격 요건 중 둘중 하나라도 null 인 경우 publish
        if (isAnalysisNeed(jobPosting)) {
            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        if (jobPosting.ocrData.isNullOrBlank()) {
                            jobPostingAnalysisService.handleContentEvent(jobPosting, null, false)
                        } else {
                            jobPostingAnalysisService.publishAnalysisEvent(jobPosting.id!!, jobPosting.ocrData)
                        }
                    }
                }
            )
        }

        upsertScrapRequest.scrapId?.let {
            scrapId ->
            val memberId = customUserDetails.getId()
            getById(scrapId).also {
                if (it.memberId != memberId){
                    throw DomainException(GlobalErrorCode.NOT_EXIST_SCRAP)
                }
            }.apply {
                jobPostingId = upsertScrapRequest.jobPostingId
            }.let {
                savedScrap -> save(savedScrap)
            }
        } ?: run {
            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        // 트랜잭션 커밋 후 성향 분석 할 것인지 확인
                        personalityAnalysisService.publishAnalysis(customUserDetails)

                        // 트랜잭션 커밋 후 Redis 랭킹 업데이트
                        redisTemplate.opsForZSet().incrementScore(
                            "scrap_ranking", upsertScrapRequest.jobPostingId.toString(), 1.0
                        )
                    }
                }
            )

            save(
                Scrap.create(
                    upsertScrapRequest.jobPostingId,
                    customUserDetails.getId(),
                    null,
                    null
                )
            )
        }
    }

    @Transactional(readOnly = true)
    fun getScrap(page : Int, size : Int, customUserDetails: CustomUserDetails) : Page<DashboardResponse> {
        val pageable = PageRequest.of(page, size)
        val memberId = customUserDetails.getId()
        val scrapPage = scrapRepository.findAllByMemberId(memberId, pageable)
        val postingMap = jobPostingRepository.findAllById(
            scrapPage.content.map { it.jobPostingId }.toSet()
        ).associateBy { it.id!! }
        val postingIds = postingMap.keys
        val memoMap : Map<Long, Memo> =
            if (postingIds.isEmpty()) emptyMap()
            else memoRepository.findAllByPostingIdInAndMemberId(postingIds, memberId)
            .associateBy { it.postingId }
        val dashboards = scrapPage.content.map { s ->
            val posting = postingMap[s.jobPostingId]
                ?: throw DomainException(GlobalErrorCode.NOT_EXIST_JOB_POSTING)

            val memoId = memoMap[s.jobPostingId]?.id
            val memo = memoId?.let { memoRepository.findById(it).get() }

            val company = companyMapper.toJsonDto(posting.company)
            val jobPostingResponse = JobPostingResponse.create(posting, company)

            val resumeFileResponse = FileResponse.create(
                s.resumeUrl,
                objectStorageService.extractOriginalFilenameFromS3(s.resumeUrl)
            )
            val portfolioFileResponse = FileResponse.create(
                s.portfolioUrl,
                objectStorageService.extractOriginalFilenameFromS3(s.portfolioUrl)
            )

            DashboardResponse.create(
                s.id,
                memoId,
                memo?.memoContent,
                jobPostingResponse,
                resumeFileResponse,
                portfolioFileResponse
            )
        }
        return PageImpl(dashboards, pageable, scrapPage.totalElements)
    }

    @Transactional(readOnly = true)
    fun getScrapCount(memberId: Long) : Long{
        return scrapRepository.countByMemberId(memberId);
    }



    private fun isAnalysisNeed(jobPosting: JobPosting) : Boolean {
        return jobPosting.qualification.isNullOrBlank() || jobPosting.preferentialTreatment.isNullOrBlank()
    }

    @Transactional
    fun scrapDeleteService(customUserDetails: CustomUserDetails, idList: List<Long>){
        val memberId = customUserDetails.getId()
        val scraps = scrapRepository.findAllById(idList)

        // 소유권 검증
        scraps.forEach { scrap ->
            if (scrap.memberId != memberId) {
                throw DomainException(GlobalErrorCode.NOT_EXIST_SCRAP)
            }
        }

        deleteByIdList(idList)
    }

    @Transactional
    fun uploadFile(
        customUserDetails: CustomUserDetails,
        scrapId: Long,
        file: MultipartFile,
        fileType: FileType
    ): FileResponse {
        val scrap = getScrapByScrapIdAndMemberId(scrapId, customUserDetails.getId())

        val fileUrl = fileType.uploadFunction(objectStorageService, file, scrapId)

        fileType.urlSetter(scrap, fileUrl)

        return FileResponse.create(fileUrl, file.originalFilename)
    }

    @Transactional
    fun deleteFile(
        customUserDetails: CustomUserDetails,
        scrapId: Long,
        fileUrl: String,
        fileType: FileType
    ): FileDeleteResponse {
        val scrap = getScrapByScrapIdAndMemberId(scrapId, customUserDetails.getId())
        if (fileType.urlGetter(scrap) != fileUrl) {
            throw DomainException(GlobalErrorCode.IS_NOT_YOUR_FILE)
        }

        objectStorageService.deleteFile(fileUrl)
        val originalFileName = objectStorageService.extractOriginalFilenameFromS3(fileUrl)
            ?: throw DomainException(GlobalErrorCode.CANNOT_EXTRACT_FILENAME)

        fileType.urlSetter(scrap, null)
        return fileType.responseFactory(true, originalFileName)
    }

    @Transactional(readOnly = true)
    fun getById(id : Long) = findById(id) ?: throw DomainException(GlobalErrorCode.NOT_EXIST_SCRAP)

    @Transactional(readOnly = true)
    fun findById(id: Long) = scrapRepository.findByIdOrNull(id)

    @Transactional
    fun save(scrap: Scrap) = scrapRepository.save(scrap)

    @Transactional
    fun deleteByIdList(idList: List<Long>) = scrapRepository.deleteAllById(idList)

    @Transactional(readOnly = true)
    fun findScrapByScrapIdAndMemberId(scrapId: Long, memberId: Long)
        = scrapRepository.findByIdAndMemberId(scrapId, memberId)

    @Transactional(readOnly = true)
    fun getScrapByScrapIdAndMemberId(scrapId: Long, memberId: Long)
        = findScrapByScrapIdAndMemberId(scrapId, memberId) ?: throw DomainException(GlobalErrorCode.NOT_EXIST_SCRAP)

    @Transactional(readOnly = true)
    fun isScrap(memberId : Long, jobPostingId : Long) : Boolean {
        return scrapRepository.findByMemberIdAndJobPostingId(memberId, jobPostingId).isPresent
    }
}