package com.zighang.core.config.rabbitmq

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("mq")
data class RabbitProperties(
    @NestedConfigurationProperty
    val dlq: DeadLetterQueue = DeadLetterQueue(),

    @NestedConfigurationProperty
    val test: TestQueue = TestQueue(),
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