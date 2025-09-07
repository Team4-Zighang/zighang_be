package com.zighang.jobposting.infrastructure.worker

import com.zighang.core.clova.util.JsonCleaner
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.scrap.dto.request.JobEnrichedEvent
import com.zighang.scrap.dto.request.JobScrapedEvent
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import com.zighang.jobposting.infrastructure.caller.JobAnalysisCaller
import com.zighang.jobposting.infrastructure.producer.JobAnalysisEventProducer
import com.zighang.scrap.infrastructure.mapper.JobAnalysisDtoMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener

class JobPostingAnalysisWorker(
    private val jobAnalysisCaller: JobAnalysisCaller,
    private val jobAnalysisDtoMapper: JobAnalysisDtoMapper,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer
) {

    @RabbitListener(queues= ["\${mq.analysis.name}"])
    fun jobPostingToClova(event : JobScrapedEvent) {
        try {
            // clova 자격요건/우대사항 분석
            val result = jobAnalysisCaller.call(event.ocrData).result.message.content

            val jobPostingAnalysisDto = jobAnalysisDtoMapper.toJsonDto(JsonCleaner.cleanJson(result))

            jobAnalysisEventProducer.publishEnriched(
                JobEnrichedEvent(
                    event.id,
                    event.memberId,
                    JobPostingAnalysisDto(
                        jobPostingAnalysisDto.qualification,
                        jobPostingAnalysisDto.preferentialTreatment,
                        jobPostingAnalysisDto.career
                    ),
                    event.isCard
                )
            )
        } catch (e: Exception) {
            throw GlobalErrorCode.CLOVA_API_CALL_FAILED.toException()
        }
    }
}