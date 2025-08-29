package com.zighang.core.config.rabbitmq

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun messageConverter(): MessageConverter {
        // DTO -> JSON -> DTO 자동 직렬화기
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter()
        return template
    }

    @Bean
    fun rabbitListenerContainerFactory(
        connectionFactory : ConnectionFactory,
        messageConverter: MessageConverter,
        customErrorMessageRecover: CustomErrorMessageRecover
    ): SimpleRabbitListenerContainerFactory {

        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(messageConverter)

        val interceptor = RetryInterceptorBuilder.stateless()
            .maxAttempts(1)     // 단 1회 재시도
            .recoverer(customErrorMessageRecover) // 실패시 DLQ로 이동
            .build()

        factory.setAdviceChain(interceptor)
        return factory
    }
}
