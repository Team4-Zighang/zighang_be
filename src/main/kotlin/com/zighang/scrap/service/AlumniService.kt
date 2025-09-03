package com.zighang.scrap.service

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.infrastructure.CompanyMapper
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import com.zighang.member.exception.MemberErrorCode
import com.zighang.member.exception.OnboardingErrorCode
import com.zighang.member.repository.MemberRepository
import com.zighang.member.repository.OnboardingRepository
import com.zighang.scrap.dto.response.AlumniSimiliarJobPostingResponseDto
import com.zighang.scrap.repository.ScrapRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AlumniService(
    private val memberRepository: MemberRepository,
    private val onboardingRepository: OnboardingRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val companyMapper: CompanyMapper
) {


    // 나와 같은 학교를 다니고 같은 직무를 가진 사람들의 북마크한 공고 살펴보기
    fun getScrappedJobPostingsBySimilarUsers(
        customUserDetails: CustomUserDetails,
        page: Int
    ) : Page<AlumniSimiliarJobPostingResponseDto> {
        val memberId = customUserDetails.getId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberErrorCode.NOT_EXIST_MEMBER.toException()

        val onboarding = member.onboardingId?.let {
            onboardingRepository.findByIdOrNull(it)
        } ?: throw OnboardingErrorCode.NOT_EXISTS_ONBOARDING.toException()

        val jobPostingsPage = jobPostingRepository.findAllScrappedJobPostingsBySimilarUsers(
            onboarding.school,
            onboarding.jobRole,
            PageRequest.of(page, 6)
        )

        return jobPostingsPage.map { jobPosting ->
            val company = companyMapper.toJsonDto(jobPosting.company)
            AlumniSimiliarJobPostingResponseDto.create(jobPosting, company)
        }
    }
}