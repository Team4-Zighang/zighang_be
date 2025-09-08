package com.zighang.jobposting.infrastructure.producer

import com.zighang.core.config.rabbitmq.config.RabbitProperties
import com.zighang.jobposting.dto.JobEnrichedEvent
import com.zighang.jobposting.dto.JobAnalysisEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class JobAnalysisEventProducer(
    private val rabbitTemplate: RabbitTemplate,
    private val rabbitProperties: RabbitProperties
) {
    fun publishAnalysis(event: JobAnalysisEvent) {
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