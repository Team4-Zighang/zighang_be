package com.zighang.card.facade

import com.zighang.card.dto.CardContentResponse
import com.zighang.card.service.CardService
import com.zighang.card.value.CardPosition
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.service.JobPostingService
import com.zighang.member.service.MemberService
import com.zighang.member.service.OnboardingService
import org.springframework.stereotype.Component

@Component
class CardFacade(
    private val cardService: CardService,
    private val memberService: MemberService,
    private val onboardingService: OnboardingService,
    private val jobPostingService: JobPostingService
) {
    fun createCard(customUserDetails: CustomUserDetails) {
        //직군, 직무 뽑아오기
        val member = memberService.getById(customUserDetails.getId())
        val onboarding = onboardingService.getById(member.onboardingId!!)
        val jobCategory = onboarding.jobCategory
        val jobRole = onboarding.jobRole

        val top3JobPosting = jobPostingService.top3ByJob(jobCategory, jobRole)

        //이전 카드 초기화
        cardService.evict(member.id)
        //카드 3개 생성
        cardService.saveTop3Ids(member.id, top3JobPosting)
    }

    fun getCard(customUserDetails: CustomUserDetails, position: CardPosition): CardContentResponse {
        // 카드 개봉하기
        val card = cardService.getCardByPosition(customUserDetails.getId(), position)

        return CardContentResponse.from(card)
    }

    fun replace(customUserDetails: CustomUserDetails, position: CardPosition) : Boolean{
        val member = memberService.getById(customUserDetails.getId())
        val onboarding = onboardingService.getById(member.onboardingId!!)
        return jobPostingService.replace(member.id, onboarding.jobCategory, onboarding.jobRole,position);
    }

    fun showOpenList(customUserDetails: CustomUserDetails): List<CardContentResponse> {
        return cardService.getOpenCardList(customUserDetails.getId());
    }


}