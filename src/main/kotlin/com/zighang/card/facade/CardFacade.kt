package com.zighang.card.facade

import com.zighang.card.dto.CardContentResponse
import com.zighang.card.dto.RemainScrapResponse
import com.zighang.card.service.CardService
import com.zighang.card.value.CardPosition
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.jobposting.service.JobPostingService
import com.zighang.member.service.JobRoleService
import com.zighang.member.service.MemberService
import com.zighang.member.service.OnboardingService
import com.zighang.scrap.service.ScrapService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CardFacade(
    private val cardService: CardService,
    private val memberService: MemberService,
    private val onboardingService: OnboardingService,
    private val jobPostingService: JobPostingService,
    private val scrapService: ScrapService,
    private val jobRoleService: JobRoleService
) {
    @Value("\${scrap.count_limit}")
    private lateinit var limitScrapCount: String

    fun createCard(customUserDetails: CustomUserDetails) {
        //직군, 직무 뽑아오기
        val member = memberService.getById(customUserDetails.getId())
        val scrapCount = scrapService.getScrapCount(member.id)
        val getCardScrapCount = cardService.getCardScrapCount(member.id)

        if(getCardScrapCount!= null && scrapCount < getCardScrapCount + limitScrapCount.toLong()) {
            throw DomainException(GlobalErrorCode.LIMIT_CARD)
        }

        val onboarding = onboardingService.getById(member.onboardingId!!)

        val jobRoleList = jobRoleService.findAllByOnboardingId(onboarding.id)
            .map { jobRole -> jobRole.jobRole }
        val leftJob = jobPostingService.filterByCareerAndJobRoleAndLowestView(member, jobRoleList, onboarding)
        val centerJob = jobPostingService.filterByCareerAndJobRoleAndLowestApply(member, jobRoleList, onboarding)
        val rightJob = jobPostingService.filterByCareerAndJobRoleAndLatest(member, jobRoleList, onboarding)

        val threeJobPosting = listOf(leftJob, centerJob, rightJob)
        println(leftJob)
        println(centerJob)
        println(rightJob)
        //이전 카드 초기화
        cardService.evict(member.id)
        //카드 3개 생성
        cardService.saveTop3Ids(member.id, threeJobPosting)
        //스크랩 수 레디스에 저장
        cardService.upsertCardScrapCount(member.id, scrapCount)
    }

    fun getCard(customUserDetails: CustomUserDetails, position: CardPosition): CardContentResponse {
        // 카드 개봉하기
        val card = cardService.getCardByPosition(customUserDetails.getId(), position)

        return CardContentResponse.from(card)
    }

    fun replace(customUserDetails: CustomUserDetails, position: CardPosition) : Boolean{
        val member = memberService.getById(customUserDetails.getId())
        val onboarding = onboardingService.getById(member.onboardingId!!)
        val jobRoleList = jobRoleService.findAllByOnboardingId(onboarding.id)
            .map { jobRole -> jobRole.jobRole }
        return jobPostingService.replace(member, jobRoleList, onboarding, position);
    }

    fun showOpenList(customUserDetails: CustomUserDetails): List<CardContentResponse> {
        return cardService.getOpenCardList(customUserDetails.getId());
    }

    fun showScrap(customUserDetails: CustomUserDetails): RemainScrapResponse {
        return cardService.getScrapForCard(customUserDetails.getId())
    }

}