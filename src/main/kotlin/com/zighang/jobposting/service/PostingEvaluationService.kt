package com.zighang.jobposting.service

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.dto.PostingEvaluationSaveResponseDto
import com.zighang.jobposting.dto.request.PostingEvaluationSaveRequestDto
import com.zighang.jobposting.entity.PostingEvaluation
import com.zighang.jobposting.exception.JobPostingErrorCode
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.jobposting.repository.PostingEvaluationRespository
import com.zighang.member.entity.Member
import com.zighang.member.exception.MemberErrorCode
import com.zighang.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class PostingEvaluationService(
    private val postingEvaluationRespository: PostingEvaluationRespository,
    private val memberRepository: MemberRepository,
    private val jobPostingRepository: JobPostingRepository
) {

    fun saveEvaluation(
        customUserDetails: CustomUserDetails,
        postingEvaluationSaveRequestDto: PostingEvaluationSaveRequestDto
    ) : PostingEvaluationSaveResponseDto {

        val currentMember = getMember(customUserDetails)

        jobPostingRepository.findById(postingEvaluationSaveRequestDto.postingId)
            .orElseThrow { throw JobPostingErrorCode.NOT_EXISTS_JOB_POSTING.toException() }

        val postingEvaluation = PostingEvaluation.create(
            currentMember.id,
            postingEvaluationSaveRequestDto.postingId,
            postingEvaluationSaveRequestDto.evalScore,
            postingEvaluationSaveRequestDto.evalText,
            postingEvaluationSaveRequestDto.recruitmentStep
        )

        postingEvaluationRespository.save(postingEvaluation)

        return PostingEvaluationSaveResponseDto.successCreate(
            postingEvaluation.id,
            postingEvaluationSaveRequestDto.postingId,
        )
    }

    fun getMember(customUserDetails: CustomUserDetails) : Member {
        return memberRepository.findById(customUserDetails.getId())
            .orElseThrow { throw MemberErrorCode.NOT_EXIST_MEMBER.toException() }
    }
}