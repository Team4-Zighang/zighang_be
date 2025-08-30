package com.zighang.scrap.infrastructure

import com.zighang.core.config.rabbitmq.config.RabbitProperties
import com.zighang.scrap.dto.request.JobEnrichedEvent
import com.zighang.scrap.dto.request.JobScrapedEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class JobAnalysisEventProducer(
    private val rabbitTemplate: RabbitTemplate,
    private val rabbitProperties: RabbitProperties
) {
    fun publishAnalysis(event: JobScrapedEvent) {
        rabbitTemplate.convertAndSend(
            rabbitProperties.analysis.exchange,
            rabbitProperties.analysis.routingKey,
            event
        )
    }

    fun publishEnriched(event: JobEnrichedEvent) {
        rabbitTemplate.convertAndSend(
            rabbitProperties.enriched.exchange,
            rabbitProperties.enriched.routingKey,
            event
        )
    }
}