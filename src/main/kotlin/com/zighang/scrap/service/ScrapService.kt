package com.zighang.scrap.service

import com.zighang.core.application.ObjectStorageService
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.memo.entity.Memo
import com.zighang.memo.repository.MemoRepository
import com.zighang.scrap.dto.request.JobScrapedEvent
import com.zighang.scrap.dto.request.UpsertScrapRequest
import com.zighang.scrap.dto.response.DashboardResponse
import com.zighang.scrap.dto.response.FileResponse
import com.zighang.scrap.dto.response.JobPostingResponse
import com.zighang.scrap.entity.Scrap
import com.zighang.scrap.infrastructure.JobAnalysisEventProducer
import com.zighang.scrap.repository.ScrapRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Service
@Slf4j
class ScrapService(
    private val scrapRepository: ScrapRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val memoRepository: MemoRepository,
    private val objectStorageService: ObjectStorageService,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer
) {

    @Transactional
    fun upsert(customUserDetails: CustomUserDetails, upsertScrapRequest: UpsertScrapRequest) {
        val jobPosting = jobPostingRepository.findById(upsertScrapRequest.jobPostingId)
            .orElseThrow{DomainException(GlobalErrorCode.NOT_EXIST_JOB_POSTING)}

        // 우대사항 / 자격 요건 중 둘중 하나라도 null 인 경우 publish
        if (isAnalysisNeed(jobPosting) && !jobPosting.ocrData.isNullOrBlank()) {
            TransactionSynchronizationManager.registerSynchronization(
                object : TransactionSynchronization {
                    override fun afterCommit() {
                        val event = JobScrapedEvent(
                            id = jobPosting.id!!,
                            ocrData = jobPosting.ocrData
                        )
                        jobAnalysisEventProducer.publishAnalysis(event)
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
                resumeUrl = upsertScrapRequest.resumeUrl
                portfolioUrl = upsertScrapRequest.portfolioUrl
            }.let {
                savedScrap -> save(savedScrap)
            }
        } ?: save(
            Scrap.create(
                upsertScrapRequest.jobPostingId,
                customUserDetails.getId(),
                upsertScrapRequest.resumeUrl,
                upsertScrapRequest.portfolioUrl
            )
        )
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
            val jobPostingResponse = JobPostingResponse.create(posting)

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
                jobPostingResponse,
                resumeFileResponse,
                portfolioFileResponse
            )
        }
        return PageImpl(dashboards, pageable, scrapPage.totalElements)
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

    @Transactional(readOnly = true)
    fun getById(id : Long) = findById(id) ?: throw DomainException(GlobalErrorCode.NOT_EXIST_SCRAP)

    @Transactional(readOnly = true)
    fun findById(id: Long) = scrapRepository.findByIdOrNull(id)

    @Transactional
    fun save(scrap: Scrap) = scrapRepository.save(scrap)

    @Transactional
    fun deleteByIdList(idList: List<Long>) = scrapRepository.deleteAllById(idList)
}