package com.zighang.scrap.service

import com.zighang.core.application.ObjectStorageService
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.memo.repository.MemoRepository
import com.zighang.scrap.dto.request.UpsertScrapRequest
import com.zighang.scrap.dto.response.DashboardResponse
import com.zighang.scrap.dto.response.FileResponse
import com.zighang.scrap.dto.response.JobPostingResponse
import com.zighang.scrap.entity.Scrap
import com.zighang.scrap.repository.ScrapRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
class ScrapService(
    private val scrapRepository: ScrapRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val memoRepository: MemoRepository,
    private val objectStorageService: ObjectStorageService
) {
    @Transactional
    fun upsert(customUserDetails: CustomUserDetails, upsertScrapRequest: UpsertScrapRequest) : Scrap? {
        jobPostingRepository.findById(upsertScrapRequest.jobPostingId)
            .orElseThrow{DomainException(GlobalErrorCode.NOT_EXIST_JOB_POSTING)}
        return upsertScrapRequest.scrapId?.let {
            scrapId -> getById(scrapId).apply {
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
        val scrapList = scrapRepository.findAllByMemberId(customUserDetails.getId())
        val dashboardList = scrapList.map { s ->
            val posting = jobPostingRepository.findById(s.jobPostingId).get()
            val memoId = memoRepository.findByPostingIdAndMemberId(s.jobPostingId, customUserDetails.getId())?.id

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
        return PageImpl(dashboardList, PageRequest.of(page, size), dashboardList.size.toLong())
    }

    @Transactional(readOnly = true)
    fun getById(id : Long) = findById(id) ?: throw DomainException(GlobalErrorCode.NOT_EXIST_SCRAP)

    @Transactional(readOnly = true)
    fun findById(id: Long) = scrapRepository.findByIdOrNull(id)

    @Transactional
    fun save(scrap: Scrap) = scrapRepository.save(scrap)
}