package com.zighang.scrap.service

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.entity.value.Company
import com.zighang.jobposting.infrastructure.CompanyMapper
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.member.exception.MemberErrorCode
import com.zighang.member.exception.OnboardingErrorCode
import com.zighang.member.repository.MemberRepository
import com.zighang.member.repository.OnboardingRepository
import com.zighang.scrap.dto.response.AlumniSimiliarJobPostingResponseDto
import com.zighang.scrap.dto.response.AlumniTop3CompanyResponseDto
import com.zighang.scrap.dto.response.AlumniTop3JobPostingScrapResponseDto
import com.zighang.scrap.repository.ScrapRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AlumniService(
    private val memberRepository: MemberRepository,
    private val onboardingRepository: OnboardingRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val companyMapper: CompanyMapper,
    private val scrapRepository: ScrapRepository
) {

    // 나와 같은 학교를 다니고 같은 직무를 가진 사람들이 많이 스크랩한 top3 공고 보여주기
    @Transactional(readOnly = true)
    fun getTop3ScrappedJobPostingsBySimilarUsers(
        customUserDetails: CustomUserDetails
    ) : List<AlumniTop3JobPostingScrapResponseDto> {
        val memberId = customUserDetails.getId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberErrorCode.NOT_EXIST_MEMBER.toException()

        val onboarding = member.onboardingId?.let {
            onboardingRepository.findByIdOrNull(it)
        } ?: throw OnboardingErrorCode.NOT_EXISTS_ONBOARDING.toException()

        val jobPostingList = jobPostingRepository.findTop3ScrappedJobPostingsBySimilarUsers(
            onboarding.school,
            onboarding.jobRole
        )

        return jobPostingList.map { jobPosting ->
            val company = companyMapper.toJsonDto(jobPosting.company)
            AlumniTop3JobPostingScrapResponseDto.create(jobPosting, company)
        }
    }

    // 기업 인기 top3
    @Transactional(readOnly = true)
    fun getTop3ScrappedCompaniesBySimilarUsers(
        customUserDetails: CustomUserDetails
    ): List<AlumniTop3CompanyResponseDto> {
        val memberId = customUserDetails.getId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberErrorCode.NOT_EXIST_MEMBER.toException()

        val onboarding = member.onboardingId?.let {
            onboardingRepository.findByIdOrNull(it)
        } ?: throw OnboardingErrorCode.NOT_EXISTS_ONBOARDING.toException()

        val similarOnboardingIds = onboardingRepository.findBySchoolAndJobRole(
            onboarding.school,
            onboarding.jobRole
        ).map { it.id }

        val similarMemberIds = memberRepository.findByOnboardingIdIn(similarOnboardingIds).map { it.id }

        // 1. 유사 사용자들이 스크랩한 공고 ID 리스트 가져오기
        val scrappedJobPostingIds = scrapRepository.findByMemberIdIn(similarMemberIds)
            .map { it.jobPostingId }
            .toSet()

        // 2. 공고 ID 리스트로 JobPosting 엔티티들 가져오기
        val jobPostings = jobPostingRepository.findAllById(scrappedJobPostingIds)

        // 3. 기업 이름별로 스크랩 횟수 집계
        val companyScrapCounts = jobPostings.groupingBy { jobPosting ->
            // JSON 문자열 파싱
            val companyData = companyMapper.toJsonDto(jobPosting.company)
            companyData.companyName ?: "알 수 없는 기업" // null인 경우를 대비
        }.eachCount()

        // 4. 스크랩 횟수 기준으로 정렬하고 상위 3개만 가져오기
        return companyScrapCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { (companyName) ->
                val companyData = jobPostings.firstOrNull {
                    companyMapper.toJsonDto(it.company).companyName == companyName
                }?.let {
                    companyMapper.toJsonDto(it.company)
                } ?: Company(companyName, null)

                AlumniTop3CompanyResponseDto.create(companyData)
            }
    }


    // 나와 같은 학교를 다니고 같은 직무를 가진 사람들의 북마크한 공고 살펴보기
    @Transactional(readOnly = true)
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