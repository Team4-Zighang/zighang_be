package com.zighang.core.config.rabbitmq

import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.retry.MessageRecoverer
import org.springframework.stereotype.Component

@Component
class CustomErrorMessageRecover(
    private val rabbitTemplate: RabbitTemplate,
    private val rabbitProperties: RabbitProperties
) : MessageRecoverer {

    // DLQ 핸들링시 DLQ에 입력 되는 데이터 형식 정리
    override fun recover(message: Message, cause: Throwable) {

        val props = message.messageProperties

        val errorMessage = mapOf(
            "error" to (cause.message ?: cause.javaClass.name),
            "errorType" to cause.javaClass.name,
            "originalQueue" to props.consumerQueue,
            "originalRoutingKey" to props.receivedRoutingKey,
            "headers" to props.headers,
        )

        rabbitTemplate.convertAndSend(
            rabbitProperties.dlq.exchange,
            rabbitProperties.dlq.routingKey,
            errorMessage
        )
    }
}