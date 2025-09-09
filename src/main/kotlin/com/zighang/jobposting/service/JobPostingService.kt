package com.zighang.jobposting.service

import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.card.dto.CardRedis
import com.zighang.card.service.CardService
import com.zighang.card.value.CardPosition
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.jobposting.dto.event.JobAnalysisEvent
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.infrastructure.producer.JobAnalysisEventProducer
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class JobPostingService(
    private val jobPostingRepository: JobPostingRepository,
    private val cardService: CardService,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer,
) {
    fun filterByCareerAndJobRoleAndLowestView(member : Member, depthTwo: List<String>, onboarding: Onboarding) : CardRedis {
        val excludedIds = cardService.getServedIds(member.id)
        val myCareer = onboarding.careerYear.year
        val firstTry = jobPostingRepository.findOneByRolesAndCareerExcludingOrderedByViewCount(
            roles = depthTwo,
            career = myCareer,
            excludedIds = excludedIds,
            excludedEmpty = excludedIds.isEmpty()
        )
        val picked = pickJobPostingOrFallback(firstTry, excludedIds)
        return toCardRedisAfterPick(member.id, picked)
    }

    fun filterByCareerAndJobRoleAndLowestApply(member: Member, depthTwo: List<String>, onboarding: Onboarding) : CardRedis {
        val excludedIds = cardService.getServedIds(member.id)
        val myCareer = onboarding.careerYear.year
        val firstTry = jobPostingRepository.findOneByRolesAndCareerExcludingOrderedByApplyCount(
            roles = depthTwo,
            career = myCareer,
            excludedIds = excludedIds,
            excludedEmpty = excludedIds.isEmpty()
        )
        val picked = pickJobPostingOrFallback(firstTry, excludedIds)
        return toCardRedisAfterPick(member.id, picked)
    }

    fun filterByCareerAndJobRoleAndLatest(member: Member, depthTwo: List<String>, onboarding: Onboarding) : CardRedis {
        val dataLimit = LocalDateTime.now().minusMonths(2)
        val excludedIds = cardService.getServedIds(member.id)
        val myCareer = onboarding.careerYear.year
        val firstTry = jobPostingRepository.findRecentByRolesAndCareerExcluding(
            roles = depthTwo,
            career = myCareer,
            excludedIds = excludedIds,
            excludedEmpty =  excludedIds.isEmpty(),
            dateLimit = dataLimit
        )
        val picked = pickJobPostingOrFallback(firstTry, excludedIds)
        return toCardRedisAfterPick(member.id, picked)
    }

    private fun pickJobPostingOrFallback(
        firstTry: List<JobPosting>,
        excludedIds: Set<Long>
    ): JobPosting {
        return if (firstTry.isNotEmpty()) {
            firstTry.first()
        } else {
            jobPostingRepository.findOneLowestViewExcluding(
                excludedIds = excludedIds,
                excludedEmpty = excludedIds.isEmpty()
            ).first()
        }
    }

    fun toCardRedisAfterPick(memberId: Long, picked: JobPosting): CardRedis {
        val jobPostingId = requireNotNull(picked.id) { "picked.id is null (unsaved entity?)" }

        // 1) 추천 이력 저장
        cardService.addServedId(memberId, jobPostingId)

        // 2) 분석 이벤트 발행
        jobAnalysisEventProducer.publishAnalysis(
            JobAnalysisEvent(jobPostingId, memberId, picked.ocrData, true)
        )

        // 3) 카드용 DTO 생성 및 CardJobPosting 생성
        val cardJobPostingAnalysisDto = CardJobPostingAnalysisDto.create(
            picked.recruitmentType,
            picked.education.displayName
        )
        val cardJobPosting = cardService.createCardJobPosting(cardJobPostingAnalysisDto, picked)

        // 4) CardRedis 조립
        return CardRedis.create(
            jobPostingId = jobPostingId,
            cardJobPosting = cardJobPosting,
            isOpen = false,
            openDateTime = null
        )
    }
    

    fun replace(member: Member, depthTwo: List<String>, onboarding: Onboarding, position: CardPosition) : Boolean{
        val top3 = cardService.getTop3Ids(member.id).toMutableList()
        val retryCard = when (position) {
            CardPosition.LEFT -> filterByCareerAndJobRoleAndLowestView(member, depthTwo, onboarding)
            CardPosition.CENTER -> filterByCareerAndJobRoleAndLowestApply(member, depthTwo, onboarding)
            CardPosition.RIGHT -> filterByCareerAndJobRoleAndLatest(member, depthTwo, onboarding)
            else -> throw DomainException(GlobalErrorCode.INVALID_POSITION_CARD)
        }
        top3[cardService.idx(position)] = retryCard
        cardService.saveTop3Ids(member.id, top3)
        return true
    }

    // 같은 직군의 공고만 모으기
    fun getJobPostingSummaryByJobCategory(
        memberId: Long,
        depthOne: String,
        postingIds: List<Long>
    ) : String {

        val pageable = PageRequest.of(0,10)
        var jobPostings =
            jobPostingRepository.findScrapedJobPostingsBydepthOneAndMemberId(
                memberId, postingIds, depthOne, pageable
            )

        // 개수 너무 적으면 전체 스크랩에서 수집
        if(jobPostings.size < 3) {
            jobPostings =
                jobPostingRepository.findScrapedJobPostingsByMemberIdAndJobPostingIds(
                    memberId, postingIds, pageable
                )
        }

        return jobPostings.joinToString("\n") { jobPosting ->
            jobPosting.summaryData
                .takeUnless { it.isBlank() }
                ?: listOfNotNull(
                    jobPosting.content,
                    jobPosting.teamInfo
                ).joinToString("\n")
                    .ifBlank { "데이터 없음" }
        }
    }
}