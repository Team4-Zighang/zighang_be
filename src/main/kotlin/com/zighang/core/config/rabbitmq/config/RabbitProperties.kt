package com.zighang.core.config.rabbitmq.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("mq")
data class RabbitProperties(
    @NestedConfigurationProperty
    val dlq: DeadLetterQueue = DeadLetterQueue(),

    @NestedConfigurationProperty
    val test: TestQueue = TestQueue(),

    @NestedConfigurationProperty
    val scraped: ScrapedQueue = ScrapedQueue(),

    @NestedConfigurationProperty
    val enriched: enrichedQueue
)

data class DeadLetterQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)

data class TestQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)

data class ScrapedQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)

data class enrichedQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)