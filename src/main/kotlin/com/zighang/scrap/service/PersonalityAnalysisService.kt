package com.zighang.scrap.service

import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.member.exception.MemberErrorCode
import com.zighang.member.exception.OnboardingErrorCode
import com.zighang.member.repository.MemberRepository
import com.zighang.member.repository.OnboardingRepository
import com.zighang.member.repository.PersonalityRepository
import com.zighang.scrap.dto.request.PersonalityAnalysisEvent
import com.zighang.scrap.dto.response.PersonalityAnalysisDto
import com.zighang.scrap.infrastructure.PersonalityAnalysisEventProducer
import com.zighang.scrap.repository.ScrapRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PersonalityAnalysisService(
    private val memberRepository: MemberRepository,
    private val onboardingRepository: OnboardingRepository,
    private val scrapRepository: ScrapRepository,
    private val personalityAnalysisEventProducer: PersonalityAnalysisEventProducer,
    private val personalityRepository: PersonalityRepository
) {

    fun publishAnalysis(customUserDetails: CustomUserDetails) {
        val memberId = customUserDetails.getId()
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberErrorCode.NOT_EXIST_MEMBER.toException()

        val scrapList = scrapRepository.findByMemberId(memberId)
        if(!isValidScrapSize(scrapList.size)) return

        val postingIds = scrapList.map { it.jobPostingId }

        val onboarding = member.onboardingId?.let {
            onboardingRepository.findByIdOrNull(it)
        } ?: throw OnboardingErrorCode.NOT_EXIST_ONBOARDING.toException()

        personalityAnalysisEventProducer.publishPersonalityAnalysis(
            PersonalityAnalysisEvent(
                memberId, onboarding.jobCategory, postingIds
            )
        )

    }

    fun getPersonalityAnalysis(customUserDetails: CustomUserDetails): PersonalityAnalysisDto {
        val personality = personalityRepository.findByMemberId(customUserDetails.getId())
            ?: throw GlobalErrorCode.NOT_EXIST_PERSONALITY.toException()

        return PersonalityAnalysisDto.create(personality)
    }

    private fun isValidScrapSize(size: Int): Boolean {
        return when {
            size <= 5 -> size == 1 || size == 3 || size == 5
            else -> (size - 5) % 5 == 0
        }
    }
}