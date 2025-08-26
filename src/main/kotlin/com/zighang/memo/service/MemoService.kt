package com.zighang.memo.service

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.logger
import com.zighang.member.repository.MemberRepository
import com.zighang.memo.dto.request.MemoCreateRequest
import com.zighang.memo.dto.response.MemoCreateResponse
import com.zighang.memo.entity.Memo
import com.zighang.memo.exception.MemoErrorCode
import com.zighang.memo.repository.MemoRepository
import lombok.extern.slf4j.Slf4j
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@Slf4j
class MemoService(
    private val memoRepository: MemoRepository,
    private val memberRepository: MemberRepository,
    private val jobPostingRepository: JobPostingRepository,
) {

    public fun saveMemo(customUserDetails: CustomUserDetails, request: MemoCreateRequest) : MemoCreateResponse{

        jobPostingRepository.findByIdOrNull(request.postingId)
            ?: throw MemoErrorCode.NOT_EXIST_POSTING.toException();

        val memberId = getMemberId(customUserDetails)

        val existingMemo = memoRepository.findByPostingIdAndMemberId(request.postingId, memberId)

        if (existingMemo != null) {
            existingMemo.update(request.content)
            memoRepository.save(existingMemo)
            return MemoCreateResponse.create(existingMemo.id!!, "메모 업데이트가 완료되었습니다.")
        } else {
            val newMemo: Memo = Memo.create(request.postingId, memberId, request.content);
            val savedMemo: Memo = memoRepository.save(newMemo)
            return MemoCreateResponse.create(savedMemo.id!!, "메모 저장이 완료되었습니다.")
        }
    }

    public fun getMemo(memoId: Long): Memo? {
        return memoRepository.findByIdOrNull(memoId)
    }

    private fun getMemberId(customUserDetails: CustomUserDetails): Long {
        return customUserDetails.getId()
    }
}