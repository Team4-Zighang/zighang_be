package com.zighang.core.config.rabbitmq

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitInfraCofig {

    // DLQ
    @Bean
    fun dlqExchange() : DirectExchange  = DirectExchange("dlq.exchange")

    @Bean
    fun dlqQueue() : Queue = Queue("dlq.queue", true)

    @Bean
    fun dlqBinding() : Binding =
        BindingBuilder.bind(dlqQueue()).to(dlqExchange()).with("dlq.routingkey")

    // Test Queue(추후 위 방식으로 핸들링 하면됨)
    @Bean
    fun testQueue(): Queue {
        return Queue(
            "test.queue", true, false, false, getDLQArgs()
        )
    }

    @Bean
    fun exchange(): DirectExchange = DirectExchange("test.exchange")

    @Bean
    fun testBinding(): Binding =
        BindingBuilder.bind(testQueue()).to(exchange()).with("test.routingKey")

    // dlq 에러 핸들링을 위해 사용하는 Args
    private fun getDLQArgs() : Map<String, String> {
        return mapOf(
            "x-dead-letter-exchange" to "dlq.exchange",
            "x-dead-letter-routing-key" to "dlq.routingkey"
        )
    }
}