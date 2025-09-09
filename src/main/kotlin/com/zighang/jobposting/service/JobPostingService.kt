package com.zighang.jobposting.service

import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.card.dto.CardRedis
import com.zighang.card.service.CardService
import com.zighang.card.value.CardPosition
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.jobposting.dto.JobAnalysisEvent
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
        println(myCareer)
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
    

    fun replace(memberId: Long, depthOne: String?, depthTwo: String?, position: CardPosition) : Boolean{
        val top3 = cardService.getTop3Ids(memberId).toMutableList()
        // 제외 대상: 현재 Top3 모든 id + 과거 노출 이력
        val exclude = mutableSetOf<Long>()
        exclude += top3.map { it.jobPostingId }
        exclude += cardService.getServedIds(memberId)

        // 새 후보 1개 찾기 (repo 구현은 예시)
        val filtered = jobPostingRepository.findNextByExcludingIdsAndDepths(
            exclude.isNotEmpty(),
            exclude.toList(),
            depthOne,
            depthTwo,
            pageable = PageRequest.of(0, 1)
        ).firstOrNull()

        // 2) 없으면 인기순으로 폴백 (예: score DESC, createdAt DESC)
        val candidate = filtered ?: jobPostingRepository.findNextByExcludingIdsOrderByPopularity(
            excluded = exclude.isNotEmpty(),
            excludedIds = exclude.toList(),
            pageable = PageRequest.of(0, 1)
        ).firstOrNull() ?: return false

        // 분석 → DTO → CardJobPosting
//        val result = analysisCaller.getCardJobResponse(candidate.ocrData).result.message.content
        jobAnalysisEventProducer.publishAnalysis(JobAnalysisEvent(candidate.id!!, memberId, candidate.ocrData, true))
        val cardJobPostingAnalysisDto = CardJobPostingAnalysisDto.create(
            candidate.recruitmentType,
            candidate.education.displayName
        )
        val cardJobPosting = cardService.createCardJobPosting(cardJobPostingAnalysisDto, candidate)

        // 새 카드로 교체 (같은 position 유지)
        val newCard = CardRedis(
            jobPostingId = candidate.id!!,
            cardJobPosting = cardJobPosting,
            isOpen = false,
            openDateTime = null,
            position = position
        )

        top3[cardService.idx(position)] = newCard
        cardService.saveTop3Ids(memberId, top3)

        cardService.addServedId(memberId, candidate.id!!)
        return true;
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