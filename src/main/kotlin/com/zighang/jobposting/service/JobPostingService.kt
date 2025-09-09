package com.zighang.jobposting.service

import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.card.dto.CardRedis
import com.zighang.card.service.CardService
import com.zighang.card.value.CardPosition
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.jobposting.dto.event.JobAnalysisEvent
import com.zighang.jobposting.dto.response.JobPostingDetailResponseDto
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.RecruitmentType
import com.zighang.jobposting.exception.JobPostingErrorCode
import com.zighang.jobposting.infrastructure.mapper.CompanyMapper
import com.zighang.jobposting.infrastructure.producer.JobAnalysisEventProducer
import com.zighang.member.entity.value.Region
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class JobPostingService(
    private val jobPostingRepository: JobPostingRepository,
//    private val analysisCaller: JobAnalysisCaller,
//    private val cardJobPosingAnalysisDtoMapper: CardJobPosingAnalysisDtoMapper,
    private val cardService: CardService,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer,
    private val companyMapper: CompanyMapper
) {
    fun top3ByJob(depthOne: String?, depthTwo: List<String>, memberId: Long): List<CardRedis> {

        val page3 = PageRequest.of(0, 3)

        val first = jobPostingRepository.findTopJobPostingsByDepths(depthOne, depthTwo, page3)
        val firstIds = first.mapNotNull { it.id }

        // 1차로 3개가 끝나면 바로 CardRedis로 변환해서 반환
        if (firstIds.size == 3) {
            return first.map {
                jobPosting ->
//                val result = analysisCaller.getCardJobResponse(jobPosting.ocrData).result.message.content
//                val jobPostingAnalysisDto = cardJobPosingAnalysisDtoMapper.toJsonDto(JsonCleaner.cleanJson(result))
                val ocrOrContent = if(jobPosting.ocrData.isBlank()) jobPosting.content else jobPosting.ocrData
                jobAnalysisEventProducer.publishAnalysis(JobAnalysisEvent(jobPosting.id!!, memberId, ocrOrContent,true))
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
            val ocrOrContent = if(jobPosting.ocrData.isBlank()) jobPosting.content else jobPosting.ocrData
            jobAnalysisEventProducer.publishAnalysis(JobAnalysisEvent(jobPosting.id!!, memberId, ocrOrContent,true))
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
        val ocrOrContent = if(candidate.ocrData.isBlank()) candidate.content else candidate.ocrData
        jobAnalysisEventProducer.publishAnalysis(JobAnalysisEvent(candidate.id!!, memberId, ocrOrContent, true))
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

    fun getOneJobPosting(postingId: Long) : JobPostingDetailResponseDto {
        val jobPosting = jobPostingRepository.findById(postingId)
            .orElseThrow{ throw JobPostingErrorCode.NOT_EXISTS_JOB_POSTING.toException() }

        val education = jobPosting.education.displayName
        val depthTwo = jobPosting.depthTwo
        val career = getCareer(jobPosting)
        val workType = getWorkType(jobPosting)
        val region = getRegion(jobPosting)

        // 이미지 url 들어가는 부분 조정하기 -> cloudfrontUrl
        val company = companyMapper.toJsonDto(jobPosting.company)


        return JobPostingDetailResponseDto(
            education = education,
            depthTwo = depthTwo,
            career = career,
            workType = workType,
            region = region,
        )
    }

    private fun getWorkType(jobPosting: JobPosting): String {
        val workTypes = jobPosting.recruitmentType.split(",")
            .mapNotNull { RecruitmentType.entries.find { enumVal -> enumVal.name == it } }

        return workTypes.joinToString(", ") {
            it.displayName
        }
    }

    private fun getRegion(jobPosting: JobPosting) : String {
        val regions = jobPosting.recruitmentRegion.split(",")
            .mapNotNull { Region.entries.find { enumVal -> enumVal.name == it } }

        return regions.joinToString (", "){
            it.regionName
        }
    }

    private fun getCareer(jobPosting: JobPosting) : String {
        return when {
            jobPosting.minCareer == -1 && jobPosting.maxCareer == -1 -> "무관"
            jobPosting.minCareer == 0 && jobPosting.maxCareer == 0 -> "신입"
            jobPosting.minCareer >= 3 && jobPosting.maxCareer <= 3 -> "경력 3년 이상"
            jobPosting.minCareer > 0 && jobPosting.maxCareer > 0 -> "경력 ${jobPosting.minCareer}-${jobPosting.maxCareer}년"
            else -> "기타"
        }
    }
}