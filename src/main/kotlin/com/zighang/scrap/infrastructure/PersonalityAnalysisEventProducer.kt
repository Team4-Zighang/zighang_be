package com.zighang.scrap.infrastructure

import com.zighang.core.config.rabbitmq.config.RabbitProperties
import com.zighang.scrap.dto.request.PersonalityAnalysisEvent
import com.zighang.scrap.dto.request.PersonalityUpdateEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class PersonalityAnalysisEventProducer(
    private val rabbitProperties: RabbitProperties,
    private val rabbitTemplate: RabbitTemplate
) {

    fun publishPersonalityAnalysis(event : PersonalityAnalysisEvent) {
        rabbitTemplate.convertAndSend(
            rabbitProperties.personality.exchange,
            rabbitProperties.personality.routingKey,
            event
        )
    }

    fun publishPersonalityUpdate(event : PersonalityUpdateEvent) {
        rabbitTemplate.convertAndSend(
            rabbitProperties.personalityUpdate.exchange,
            rabbitProperties.personalityUpdate.routingKey,
            event
        )
    }
}