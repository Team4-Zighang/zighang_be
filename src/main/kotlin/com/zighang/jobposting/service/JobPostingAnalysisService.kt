package com.zighang.jobposting.service

import com.zighang.jobposting.dto.event.JobAnalysisEvent
import com.zighang.jobposting.dto.event.JobEnrichedEvent
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.infrastructure.mapper.ContentMapper
import com.zighang.jobposting.infrastructure.producer.JobAnalysisEventProducer
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import org.springframework.stereotype.Service

@Service
class JobPostingAnalysisService(
    private val contentMapper: ContentMapper,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer,
) {

    fun handleContentEvent(jobPosting: JobPosting, memberId: Long?, isCard: Boolean) {
        val content = jobPosting.content?.trimStart()

        // html의 경우 값 채워져있음 -> 분석 로직 스킵
        if (content?.startsWith("<") == false) {
            val jobEnrichedEvent = toJobEnrichedEvent(jobPosting, memberId, isCard)
            jobEnrichedEvent?.let { jobAnalysisEventProducer.publishEnriched(it) }
        }
    }

    fun publishAnalysisEvent(id: Long, data: String) {
        val event = JobAnalysisEvent(id = id, ocrData = data)
        jobAnalysisEventProducer.publishAnalysis(event)
    }

    private fun toJobEnrichedEvent(jobPosting: JobPosting, memberId: Long?, isCard: Boolean): JobEnrichedEvent? {
        val content = try {
            contentMapper.toJsonDto(jobPosting.content!!)
        } catch (ex: Exception) {
            return null
        }

        val dto = content.requirements?.let { requirements ->
            JobPostingAnalysisDto(
                qualification = requirements,
                preferentialTreatment = content.prefferedPoints ?: ""
            )
        }

        return dto?.let {
            JobEnrichedEvent(
                id = jobPosting.id!!,
                memberId = memberId,
                jobPostingAnalysisDto = it,
                isCard = isCard
            )
        }
    }
}
