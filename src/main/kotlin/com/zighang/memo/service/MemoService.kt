package com.zighang.memo.service

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.memo.dto.request.MemoCreateRequest
import com.zighang.memo.dto.response.MemoCreateResponse
import com.zighang.memo.entity.Memo
import com.zighang.memo.exception.MemoErrorCode
import com.zighang.memo.repository.MemoRepository
import com.zighang.scrap.dto.request.JobScrapedEvent
import com.zighang.scrap.dto.request.UpsertScrapRequest
import com.zighang.scrap.entity.Scrap
import com.zighang.scrap.infrastructure.JobAnalysisEventProducer
import com.zighang.scrap.repository.ScrapRepository
import com.zighang.scrap.service.ScrapService
import jakarta.transaction.Transactional
import lombok.extern.slf4j.Slf4j
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@Slf4j
class MemoService(
    private val memoRepository: MemoRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val scrapRepository: ScrapRepository,
    private val scrapService: ScrapService
) {
    
    @Transactional
    fun saveMemo(
        customUserDetails: CustomUserDetails,
        request: MemoCreateRequest
    ) : MemoCreateResponse{

        jobPostingRepository.findByIdOrNull(request.postingId)
            ?: throw MemoErrorCode.NOT_EXIST_POSTING.toException();

        val memberId = getMemberId(customUserDetails)

        // 메모 저장시 스크랩이 안되어 있다면 같이 저장
        getJobPostingByPostingIdAndMemberId(request.postingId, memberId) ?: run {
            scrapService.upsert(
                customUserDetails,
                UpsertScrapRequest(
                    jobPostingId = request.postingId,
                    scrapId = null,
                    resumeUrl = null,
                    portfolioUrl = null
                )
            )
        }

        memoRepository.findByPostingIdAndMemberId(request.postingId, memberId)?.let{
            it.update(request.content)
            memoRepository.save(it)
            return MemoCreateResponse.create(it.id!!, "메모 업데이트가 완료되었습니다.")
        } ?: run {
            val newMemo: Memo = Memo.create(request.postingId, memberId, request.content)
            val savedMemo: Memo = memoRepository.save(newMemo)
            return MemoCreateResponse.create(savedMemo.id!!, "메모 저장이 완료되었습니다.")
        }
    }
    
    
    // 공고당 메모 조회
    fun getMemo(customUserDetails: CustomUserDetails, postingId: Long): String? {
        val savedMemo = memoRepository.findByPostingIdAndMemberId(postingId, getMemberId(customUserDetails))
        return savedMemo?.memoContent
    }

    private fun getMemberId(customUserDetails: CustomUserDetails): Long {
        return customUserDetails.getId()
    }

    private fun getJobPostingByPostingIdAndMemberId(postingId: Long, memberId: Long): Scrap? {
        return scrapRepository.findByJobPostingIdAndMemberId(postingId, memberId)
    }
}