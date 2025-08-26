package com.zighang.memo.service

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.memo.dto.request.MemoCreateRequest
import com.zighang.memo.dto.response.MemoCreateResponse
import com.zighang.memo.entity.Memo
import com.zighang.memo.exception.MemoErrorCode
import com.zighang.memo.repository.MemoRepository
import jakarta.transaction.Transactional
import lombok.extern.slf4j.Slf4j
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@Slf4j
class MemoService(
    private val memoRepository: MemoRepository,
    private val jobPostingRepository: JobPostingRepository,
) {
    
    // 추후 스크랩 추가시 스크랩 자동으로 되는 로직 추가해야 함
    @Transactional
    fun saveMemo(customUserDetails: CustomUserDetails, request: MemoCreateRequest) : MemoCreateResponse{

        jobPostingRepository.findByIdOrNull(request.postingId)
            ?: throw MemoErrorCode.NOT_EXIST_POSTING.toException();

        val memberId = getMemberId(customUserDetails)

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
}