package com.zighang.core.config.rabbitmq

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitInfraCofig(
    val rabbitProperties: RabbitProperties
) {

    // DLQ
    @Bean
    fun dlqExchange() : DirectExchange  = DirectExchange(rabbitProperties.dlq.exchange)

    @Bean
    fun dlqQueue() : Queue = Queue(rabbitProperties.dlq.name, true)

    @Bean
    fun dlqBinding() : Binding =
        BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with(rabbitProperties.dlq.routingKey)

    // Test Queue(추후 위 방식으로 핸들링 하면됨)
    @Bean
    fun testQueue(): Queue {
        return Queue(
            rabbitProperties.test.name, true, false, false, getDLQArgs()
        )
    }

    @Bean
    fun testExchange(): DirectExchange = DirectExchange(rabbitProperties.test.exchange)

    @Bean
    fun testBinding(): Binding =
        BindingBuilder.bind(testQueue()).to(testExchange()).with(rabbitProperties.test.routingKey)

    // dlq 에러 핸들링을 위해 사용하는 Args
    private fun getDLQArgs() : Map<String, String> {
        return mapOf(
            "x-dead-letter-exchange" to rabbitProperties.dlq.exchange,
            "x-dead-letter-routing-key" to rabbitProperties.dlq.routingKey
        )
    }
}