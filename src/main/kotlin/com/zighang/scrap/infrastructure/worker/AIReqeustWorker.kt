package com.zighang.scrap.infrastructure.worker

import com.zighang.core.clova.util.JsonCleaner
import com.zighang.core.config.rabbitmq.config.RabbitProperties
import com.zighang.scrap.dto.request.JobEnrichedEvent
import com.zighang.scrap.dto.request.JobScrapedEvent
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import com.zighang.scrap.infrastructure.JobAnalysisCaller
import com.zighang.scrap.infrastructure.JobAnalysisEventProducer
import com.zighang.scrap.infrastructure.mapper.JobAnalysisDtoMapper
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
@Slf4j
class AIReqeustWorker(
    private val jobAnalysisEventProducer: JobAnalysisEventProducer,
    private val jobAnalysisCaller: JobAnalysisCaller,
    private val jobAnalysisDtoMapper: JobAnalysisDtoMapper
) {

    private val log = LoggerFactory.getLogger(AIReqeustWorker::class.java)

    @RabbitListener(queues= ["\${mq.analysis.name}"])
    fun jobPostingToClova(event : JobScrapedEvent) {
        // clova 자격 우대사항 요건
        val result = jobAnalysisCaller.call(event.ocrData).result.message.content

        val jobPostingAnalysisDto = jobAnalysisDtoMapper.toJsonDto(JsonCleaner.cleanJson(result))

        log.info(jobPostingAnalysisDto.toString())

        jobAnalysisEventProducer.publishEnriched(
            JobEnrichedEvent(
                event.id,
                JobPostingAnalysisDto(
                    jobPostingAnalysisDto.qualification, jobPostingAnalysisDto.preferentialTreatment
                )
            )
        )
        log.info("publish : analysis -> enrich")
    }

    @RabbitListener(queues= ["\${mq.enriched.name}"])
    fun jobEnriched(event : JobEnrichedEvent) {
        log.info("enrich consumer : $event")

        // DB 업데이트 로직
    }
}