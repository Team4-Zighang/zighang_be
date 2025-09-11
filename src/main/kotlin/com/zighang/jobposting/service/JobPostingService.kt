package com.zighang.jobposting.service

import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.card.dto.CardRedis
import com.zighang.card.service.CardService
import com.zighang.card.value.CardPosition
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.jobposting.dto.event.JobAnalysisEvent
import com.zighang.jobposting.dto.response.JobPostingDetailResponseDto
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.exception.JobPostingErrorCode
import com.zighang.jobposting.infrastructure.mapper.CompanyMapper
import com.zighang.jobposting.infrastructure.mapper.ContentMapper
import com.zighang.jobposting.infrastructure.producer.JobAnalysisEventProducer
import com.zighang.jobposting.util.*
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class JobPostingService(
    private val jobPostingRepository: JobPostingRepository,
    private val cardService: CardService,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer,
    private val companyMapper: CompanyMapper,
    private val contentMapper: ContentMapper,
) {
    @Value("\${cloudfront.url}")
    private lateinit var cloudfrontUrl: String

    fun filterByCareerAndJobRoleAndLowestView(member : Member, depthTwo: List<String>, onboarding: Onboarding) : CardRedis {
        val excludedIds = cardService.getServedIds(member.id)
        val myCareer = onboarding.careerYear.year
        val shuffledDepthTwo = depthTwo.shuffled()
        for(sdt in shuffledDepthTwo) {
            val firstTry = jobPostingRepository.findOneByRolesAndCareerExcludingOrderedByViewCount(
                role = sdt,
                career = myCareer,
                excludedIds = excludedIds,
                excludedEmpty = excludedIds.isEmpty()
            )
            if(firstTry.isNotEmpty()) {
                return toCardRedisAfterPick(member.id, firstTry[0])
            }
        }
        val picked = pickJobPostingOrFallback(excludedIds)
        return toCardRedisAfterPick(member.id, picked)
    }

    fun filterByCareerAndJobRoleAndLowestApply(member: Member, depthTwo: List<String>, onboarding: Onboarding) : CardRedis {
        val excludedIds = cardService.getServedIds(member.id)
        val myCareer = onboarding.careerYear.year
        val shuffledDepthTwo = depthTwo.shuffled()
        for(sdt in shuffledDepthTwo) {
            val firstTry = jobPostingRepository.findOneByRolesAndCareerExcludingOrderedByApplyCount(
                role = sdt,
                career = myCareer,
                excludedIds = excludedIds,
                excludedEmpty = excludedIds.isEmpty()
            )
            if(firstTry.isNotEmpty()) {
                return toCardRedisAfterPick(member.id, firstTry[0])
            }
        }
        val picked = pickJobPostingOrFallback(excludedIds)
        return toCardRedisAfterPick(member.id, picked)
    }

    fun filterByCareerAndJobRoleAndLatest(member: Member, depthTwo: List<String>, onboarding: Onboarding) : CardRedis {
        val dataLimit = LocalDateTime.now().minusMonths(2)
        val excludedIds = cardService.getServedIds(member.id)
        val myCareer = onboarding.careerYear.year
        val shuffledDepthTwo = depthTwo.shuffled()
        for(sdt in shuffledDepthTwo) {
            val firstTry = jobPostingRepository.findRecentByRolesAndCareerExcluding(
                role = sdt,
                career = myCareer,
                excludedIds = excludedIds,
                excludedEmpty =  excludedIds.isEmpty(),
                dateLimit = dataLimit
            )
            if(firstTry.isNotEmpty()) {
                return toCardRedisAfterPick(member.id, firstTry[0])
            }
        }
        val picked = pickJobPostingOrFallback(excludedIds)
        return toCardRedisAfterPick(member.id, picked)
    }

    private fun pickJobPostingOrFallback(
        excludedIds: Set<Long>
    ): JobPosting {
        return jobPostingRepository.findOneLowestViewExcluding(
            excludedIds = excludedIds,
            excludedEmpty = excludedIds.isEmpty()
        ).first()
    }

    fun toCardRedisAfterPick(memberId: Long, picked: JobPosting): CardRedis {
        val jobPostingId = requireNotNull(picked.id) { "picked.id is null (unsaved entity?)" }

        // 1) 추천 이력 저장
        cardService.addServedId(memberId, jobPostingId)

        // 2) 분석 이벤트 발행
        if(picked.ocrData != null) {
            jobAnalysisEventProducer.publishAnalysis(
                JobAnalysisEvent(jobPostingId, memberId, picked.ocrData, true)
            )
        }
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

        val pageable = PageRequest.of(0, 10)
        var jobPostings =
            jobPostingRepository.findScrapedJobPostingsBydepthOneAndMemberId(
                memberId, postingIds, depthOne, pageable
            )

        // 개수 너무 적으면 전체 스크랩에서 수집
        if (jobPostings.size < 3) {
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
            .orElseThrow { throw JobPostingErrorCode.NOT_EXISTS_JOB_POSTING.toException() }

        val education = jobPosting.education.displayName
        val depthTwo = jobPosting.depthTwo?.takeIf { it.isNotBlank() }
        val career = getCareer(jobPosting)
        val workType = getWorkType(jobPosting)
        val region = getRegion(jobPosting)

        // 이미지 url 들어가는 부분 조정하기 -> cloudfrontUrl
        val company = companyMapper.toJsonDto(jobPosting.company).apply {
            companyImageUrl = companyImageUrl?.let {
                if (it.startsWith("http")) it else cloudfrontUrl + it
            }
        }

        // content 관련 설정
        val recruitmentImageUrl: String? = jobPosting.recruitmentImageUrl
            ?.takeIf { it.isNotBlank() }

        val htmlTagRegex = Regex("^\\s*<\\w+.*?>")

        val content = jobPosting.content
            .ifBlank { null }
            ?.let {
                if (htmlTagRegex.containsMatchIn(it)) {
                    // HTML 태그로 시작하는 경우 -> 필요한 항목들 렌더링 가능하게 보내주기
                    listOf(
                        jobPosting.content,
                        jobPosting.jobDescription,
                        jobPosting.qualification,
                        jobPosting.preferentialTreatment,
                        jobPosting.recruitmentProcess
                    ).mapNotNull { it.takeIf { s -> !s.isNullOrBlank() } }
                        .joinToString("<br>")
                } else {
                    // 일반 텍스트
                    // mapper로 변환처리
                    ContentHtmlConverter.toHtml(contentMapper.toJsonDto(jobPosting.content))
                }
            }

        jobPosting.updateViewCount()

        return JobPostingDetailResponseDto(
            postingId = jobPosting.id!!,
            title = jobPosting.title,
            education = education,
            depthTwo = depthTwo,
            career = career,
            workType = workType,
            region = region,
            company = company,
            viewCount = jobPosting.viewCount,
            recruitmentImageUrl = recruitmentImageUrl,
            recruitmentContent = content,
            recruitmentOriginalUrl = jobPosting.recruitmentOriginalUrl,
            uploadDate = formatPostingDate(jobPosting.uploadDate, "start"),
            expiredDate = formatPostingDate(jobPosting.recruitmentEndDate, "end")
        )
    }
}
