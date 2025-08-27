package com.zighang.core.config.rabbitmq

import com.zighang.core.clova.dto.ChatRequest
import com.zighang.core.clova.dto.ClovaMessage
import com.zighang.core.clova.dto.ClovaStudioRequest
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class TestEventPublisher(
    private val rabbitTemplate: RabbitTemplate
) {

    fun testPublisher(event: ChatRequest) {
        val clovaStudioRequest: ClovaStudioRequest = ClovaStudioRequest(
            listOf(
                ClovaMessage.createSystemMessage(event.systemMessage),
                ClovaMessage.createUserMessage(event.userMessage)
            ),
            512
        )

        rabbitTemplate.convertAndSend("test.exchange", "test.routingKey", clovaStudioRequest)
        println("publish test : $clovaStudioRequest")
    }
}