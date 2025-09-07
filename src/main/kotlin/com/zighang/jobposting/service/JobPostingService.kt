package com.zighang.jobposting.service

import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.card.dto.CardRedis
import com.zighang.card.service.CardService
import com.zighang.card.value.CardPosition
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.scrap.dto.request.JobScrapedEvent
import com.zighang.jobposting.infrastructure.producer.JobAnalysisEventProducer
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class JobPostingService(
    private val jobPostingRepository: JobPostingRepository,
//    private val analysisCaller: JobAnalysisCaller,
//    private val cardJobPosingAnalysisDtoMapper: CardJobPosingAnalysisDtoMapper,
    private val cardService: CardService,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer
) {
    fun top3ByJob(depthOne: String?, depthTwo: String?, memberId: Long): List<CardRedis> {

        val page3 = PageRequest.of(0, 3)

        val first = jobPostingRepository.findTopJobPostingsByDepths(depthOne, depthTwo, page3)
        val firstIds = first.mapNotNull { it.id }

        // 1차로 3개가 끝나면 바로 CardRedis로 변환해서 반환
        if (firstIds.size == 3) {
            return first.map {
                jobPosting ->
//                val result = analysisCaller.getCardJobResponse(jobPosting.ocrData).result.message.content
//                val jobPostingAnalysisDto = cardJobPosingAnalysisDtoMapper.toJsonDto(JsonCleaner.cleanJson(result))
                jobAnalysisEventProducer.publishAnalysis(JobScrapedEvent(jobPosting.id!!, memberId, jobPosting.ocrData,true))
                val cardJobPostingAnalysisDto = CardJobPostingAnalysisDto.create(
                    jobPosting.career,
                    jobPosting.recruitmentType,
                    jobPosting.education.displayName
                )
                val cardJobPosting = cardService.createCardJobPosting(cardJobPostingAnalysisDto, jobPosting)
                CardRedis.create(jobPostingId = jobPosting.id!!, cardJobPosting, isOpen = false, openDateTime = null)
            }
        }

        // 부족분 보충
        val need = 3 - firstIds.size
        val second = if (need > 0) {
            jobPostingRepository.findTopJobPostingsExcludingIds(
                excluded = firstIds.isNotEmpty(),
                excludedIds = firstIds,
                pageable = PageRequest.of(0, need)
            )
        } else {
            emptyList()
        }
        val merged = (first + second)
            .distinctBy { it.id }
            .take(3)
        // 합치고, 중복 제거, 최대 3개까지 자르고, CardRedis로 변환
        return merged.map{ jobPosting ->
            jobAnalysisEventProducer.publishAnalysis(JobScrapedEvent(jobPosting.id!!, memberId, jobPosting.ocrData,true))
            val cardJobPostingAnalysisDto = CardJobPostingAnalysisDto.create(
                jobPosting.career,
                jobPosting.recruitmentType,
                jobPosting.education.displayName
            )
            val cardJobPosting = cardService.createCardJobPosting(cardJobPostingAnalysisDto, jobPosting)
            CardRedis.create(
                jobPostingId = jobPosting.id!!,
                cardJobPosting = cardJobPosting,
                isOpen = false,
                openDateTime = null
            )
        }
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
        jobAnalysisEventProducer.publishAnalysis(JobScrapedEvent(candidate.id!!, memberId, candidate.ocrData, true))
        val cardJobPostingAnalysisDto = CardJobPostingAnalysisDto.create(
            candidate.career,
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
}