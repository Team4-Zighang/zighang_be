package com.zighang.scrap.infrastructure.worker

import com.zighang.core.config.rabbitmq.config.RabbitProperties
import com.zighang.scrap.dto.request.JobEnrichedEvent
import com.zighang.scrap.dto.request.JobScrapedEvent
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import com.zighang.scrap.infrastructure.JobAnalysisEventProducer
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
@Slf4j
class AIReqeustWorker(
    private val jobAnalysisEventProducer: JobAnalysisEventProducer
) {

    private val log = LoggerFactory.getLogger(AIReqeustWorker::class.java)

    @RabbitListener(queues= ["\${mq.scraped.name}"])
    fun jobPostingToClova(event : JobScrapedEvent) {
        log.info("analysis consumer : $event")

        // clova 자격 우대사항 요건

        jobAnalysisEventProducer.publishEnriched(
            JobEnrichedEvent(
                event.id,
                JobPostingAnalysisDto(
                    "자격요건", "우대사항"
                )
            )
        )
    }

    @RabbitListener(queues= ["\${mq.enriched.name}"])
    fun jobEnriched(event : JobEnrichedEvent) {
        log.info("enrich consumer : $event")

        // DB 업데이트 로직
    }
}