package com.zighang.core.config.rabbitmq.config

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.retry.MessageRecoverer
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomErrorMessageRecover(
    private val rabbitTemplate: RabbitTemplate,
    private val rabbitProperties: RabbitProperties
) : MessageRecoverer {

    companion object {
        private val log = LoggerFactory.getLogger(CustomErrorMessageRecover::class.java)
    }

    // DLQ 핸들링시 DLQ에 입력 되는 데이터 형식 정리
    override fun recover(message: Message, cause: Throwable) {
        val props = message.messageProperties
        val safeHeaders = props.headers.mapValues { (_, v) ->
            when(v) {
                is String, is Number, is Boolean, null -> v
                else -> v.toString()
            }
        }

        val bodyText = runCatching { String(message.body, Charsets.UTF_8) }
            .getOrElse { Base64.getEncoder().encodeToString(message.body) }

        val errorMessage = mapOf(
            "error" to (cause.message ?: cause.javaClass.name),
            "errorType" to cause.javaClass.name,
            "stackTrace" to cause.stackTraceToString(),
            "originalQueue" to props.consumerQueue,
            "originalRoutingKey" to props.receivedRoutingKey,
            "originalExchange" to props.receivedExchange,
            "headers" to safeHeaders,
            "body" to bodyText,
            "timestamp" to java.time.Instant.now().toString(),
        )
        try {
            rabbitTemplate.convertAndSend(
                rabbitProperties.dlq.exchange,
                rabbitProperties.dlq.routingKey,
                errorMessage
            )
        } catch(e: Exception) {
            log.error(
                "DLQ 전송 실패: exchange={}, routingKey={}, msgProps={}",
                rabbitProperties.dlq.exchange, rabbitProperties.dlq.routingKey, props, e
            )
        }
    }
}