package com.zighang.core.config.rabbitmq.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitInfraConfig(
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

    // 자격요건 / 우대사항 to Clova Queue
    @Bean
    fun jobScrapedQueue(): Queue {
        return Queue(
            rabbitProperties.scraped.name, true, false, false, getDLQArgs()
        )
    }

    @Bean
    fun jobScrapedExchange(): DirectExchange = DirectExchange(rabbitProperties.scraped.exchange)

    @Bean
    fun jobScrapedBinding() : Binding =
        BindingBuilder.bind(jobScrapedQueue()).to(jobScrapedExchange()).with(rabbitProperties.scraped.routingKey)

    // clova to update DB Queue
    @Bean
    fun jobEnrichedQueue() : Queue {
        return Queue(
            rabbitProperties.enriched.name, true, false, false, getDLQArgs()
        )
    }

    @Bean
    fun jobEnrichedExchange(): DirectExchange = DirectExchange(rabbitProperties.enriched.exchange)

    @Bean
    fun jobEnrichedBinding() :Binding =
        BindingBuilder.bind(jobEnrichedQueue()).to(jobEnrichedExchange()).with(rabbitProperties.enriched.routingKey)

    // dlq 에러 핸들링을 위해 사용하는 Args
    private fun getDLQArgs() : Map<String, String> {
        return mapOf(
            "x-dead-letter-exchange" to rabbitProperties.dlq.exchange,
            "x-dead-letter-routing-key" to rabbitProperties.dlq.routingKey
        )
    }
}