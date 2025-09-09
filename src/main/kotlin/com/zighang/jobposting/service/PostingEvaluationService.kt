package com.zighang.jobposting.service

import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.dto.response.PostingEvaluationSaveResponseDto
import com.zighang.jobposting.dto.request.PostingEvaluationSaveRequestDto
import com.zighang.jobposting.dto.response.PostingEvaluationDetailResponseDto
import com.zighang.jobposting.dto.response.PostingEvaluationListResponseDto
import com.zighang.jobposting.entity.PostingEvaluation
import com.zighang.jobposting.exception.JobPostingErrorCode
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.jobposting.repository.PostingEvaluationRespository
import com.zighang.member.entity.Member
import com.zighang.member.exception.MemberErrorCode
import com.zighang.member.repository.MemberRepository
import com.zighang.member.repository.OnboardingRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service

@Service
class PostingEvaluationService(
    private val postingEvaluationRespository: PostingEvaluationRespository,
    private val memberRepository: MemberRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val onboardingRepository: OnboardingRepository
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

    fun getEvaluationList(
        customUserDetails: CustomUserDetails,
        postingId: Long,
        page: Int
    ) : PostingEvaluationListResponseDto {

        val currentMember = getMember(customUserDetails)

        val onboarding = currentMember.onboardingId?.let { onboardingRepository.findById(it) }
            ?: throw GlobalErrorCode.NOT_EXIST_ONBOARDING.toException()

        val allEvaluations = postingEvaluationRespository
            .findByPostingIdAndSchool(postingId, onboarding.get().school)
        // 본인 것 보여줘도 되나?
//            .filter { it.memberId != currentMember.id }

        val totalCount = allEvaluations.size
        val avgScore = if (allEvaluations.isNotEmpty()) {
            allEvaluations.map { it.evalScore }.average()
        } else 0.0

        // 페이지네이션 처리
        val pageable = PageRequest.of(page, 10)
        val start = pageable.pageNumber * pageable.pageSize
        val end = minOf(start + pageable.pageSize, totalCount)
        val content = if (start < end) allEvaluations.subList(start, end) else emptyList()

        // major 매핑용
        val memberIds = allEvaluations.map { it.memberId }.toSet()
        val memberMap = memberRepository.findAllById(memberIds)
            .associate { it.id to it.onboardingId }

        val onboardingIds = memberMap.values.toSet()
        val onboardingMap = onboardingRepository.findAllById(onboardingIds)
            .associate { it.id to (it.major) }

        val slice = SliceImpl(
            content.map {
                val onboardingId = memberMap[it.memberId]
                val major = onboardingMap[onboardingId] ?: "미기재"

                PostingEvaluationDetailResponseDto(
                    score = it.evalScore,
                    major = major,
                    createdAt = it.createdAt.toString(),
                    recruitmentStep = it.recruitmentStep.displayValue,
                    evalText = it.evalText
                )
            },
            pageable,
            end < totalCount
        )

        return PostingEvaluationListResponseDto(
            schoolName = onboarding.get().school.schoolName,
            avgScore = avgScore,
            totalCount = totalCount,
            evalList = slice
        )

    }

    fun getMember(customUserDetails: CustomUserDetails) : Member {
        return memberRepository.findById(customUserDetails.getId())
            .orElseThrow { throw MemberErrorCode.NOT_EXIST_MEMBER.toException() }
    }
}