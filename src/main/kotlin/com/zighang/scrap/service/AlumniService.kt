package com.zighang.scrap.service

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.entity.value.Company
import com.zighang.jobposting.infrastructure.CompanyMapper
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.member.exception.MemberErrorCode
import com.zighang.member.exception.OnboardingErrorCode
import com.zighang.member.repository.MemberRepository
import com.zighang.member.repository.OnboardingRepository
import com.zighang.scrap.dto.response.alumni.AlumniSimiliarJobPostingResponseDto
import com.zighang.scrap.dto.response.alumni.AlumniTop3CompanyResponseDto
import com.zighang.scrap.dto.response.alumni.AlumniTop3JobPostingScrapResponseDto
import com.zighang.scrap.dto.response.alumni.SimilarAlumniResponseDto
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
        } ?: throw OnboardingErrorCode.NOT_EXIST_ONBOARDING.toException()

        val myScrappedJobPostingIds =
            scrapRepository.findByMemberId(memberId).map { it.jobPostingId }

        val jobPostingList = jobPostingRepository.findTop3ScrappedJobPostingsBySimilarUsers(
            onboarding.school,
            onboarding.jobRole
        )

        return jobPostingList.map { jobPosting ->
            val company = companyMapper.toJsonDto(jobPosting.company)
            val isSaved = myScrappedJobPostingIds.contains(jobPosting.id)
            AlumniTop3JobPostingScrapResponseDto.create(jobPosting, company, isSaved)
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
        } ?: throw OnboardingErrorCode.NOT_EXIST_ONBOARDING.toException()

        val similarOnboardingIds = onboardingRepository.findBySchoolAndJobRole(
            onboarding.school,
            onboarding.jobRole
        ).map { it.id }

        val similarMemberIds = memberRepository.findByOnboardingIdIn(similarOnboardingIds).map { it.id }

        // 1. 유사 사용자들이 스크랩한 공고 ID 리스트 가져오기
        val scrappedJobPostingIds: List<Long> = scrapRepository.findByMemberIdIn(similarMemberIds)
            .map { it.jobPostingId }
        if (scrappedJobPostingIds.isEmpty()) return emptyList()

        // 2. 조회 최적화: 조회할 공고 ID만 distinct 하여 엔티티 조회
        val jobPostingsById = jobPostingRepository.findAllById(scrappedJobPostingIds.toSet())
            .associateBy { it.id }

        // 3. 공고ID -> 회사명, 회사데이터 사전 구축 (toJsonDto 중복 파싱 방지)
        val companyNameByPostingId = mutableMapOf<Long, String>()
        val companyDataByName = mutableMapOf<String, Company>()
        jobPostingsById.values.forEach { jobPosting ->
            val companyDto = companyMapper.toJsonDto(jobPosting.company)
            val companyName = companyDto.companyName ?: "알 수 없는 기업"
            jobPosting.id?.let { nonNullId ->
                companyNameByPostingId[nonNullId] = companyName
            }
            companyDataByName.putIfAbsent(companyName, companyDto) // 동일 회사명은 대표 1건만 보관
        }

        // 4. 회사명별 스크랩 빈도 집계
        val companyScrapCounts = scrappedJobPostingIds
            .mapNotNull { companyNameByPostingId[it] }
            .groupingBy { it }
            .eachCount()

        // 5. 스크랩 빈도 기준으로 정렬하고 상위 3개만 DTO로 변환
        return companyScrapCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { (companyName) ->
                val companyData = companyDataByName[companyName] ?: Company(companyName, null)
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
        } ?: throw OnboardingErrorCode.NOT_EXIST_ONBOARDING.toException()

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

    // 나와 같은 직무를 희망하는 동문 리스트
    @Transactional(readOnly = true)
    fun getAlumniBySimilarUsers(
        customUserDetails: CustomUserDetails
    ) : List<SimilarAlumniResponseDto> {

        val memberId = customUserDetails.getId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberErrorCode.NOT_EXIST_MEMBER.toException()

        val onboarding = member.onboardingId?.let {
            onboardingRepository.findByIdOrNull(it)
        } ?: throw OnboardingErrorCode.NOT_EXIST_ONBOARDING.toException()

        // 같은 학교, 같은 직무를 가진 동문 ID 리스트 가져오기
        val similarOnboardingIds = onboardingRepository.findBySchoolAndJobRole(
            onboarding.school,
            onboarding.jobRole
        ).map { it.id }

        val allSimilarMemberIds = memberRepository.findByOnboardingIdIn(similarOnboardingIds).map { it.id }

        // 스크랩 공고가 4개 이상인 멤버 ID만 추리기
        val filteredMemberIds = scrapRepository.findMemberIdsWithMoreThanFourScraps(allSimilarMemberIds)

        val similarMembers = memberRepository.findAllById(filteredMemberIds).associateBy { it.id }
        val similarOnboardings = onboardingRepository.findAllById(similarMembers.values.mapNotNull { it.onboardingId }).associateBy { it.id }
        val allScraps = scrapRepository.findByMemberIdIn(filteredMemberIds)
        val jobPostings = jobPostingRepository.findAllById(allScraps.map { it.jobPostingId }.toSet()).associateBy { it.id }

        val results = mutableListOf<SimilarAlumniResponseDto>()

        for (filteredMemberId in filteredMemberIds) {
            val currentMember = similarMembers[filteredMemberId]!!
            val currentOnboarding = similarOnboardings[currentMember.onboardingId!!]!!

            // 스크랩한 공고의 기업 이미지 4개 추출
            val scrappedJobPostingCompanys = allScraps
                .filter { it.memberId == filteredMemberId }
                .mapNotNull { scrap -> jobPostings[scrap.jobPostingId]?.company?.let { companyMapper.toJsonDto(it) } }
                .take(4) // 상위 4개만 가져옴

            results.add(
                SimilarAlumniResponseDto(
                    memberId = currentMember.id,
                    memberName = currentMember.name,
                    school = currentOnboarding.school.schoolName,
                    jobRole = currentOnboarding.jobRole,
                    companyLists = scrappedJobPostingCompanys
                )
            )
        }
        return results
    }
}