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
    val analysis: AnalysisQueue = AnalysisQueue(),

    @NestedConfigurationProperty
    val enriched: EnrichedQueue = EnrichedQueue(),

    @NestedConfigurationProperty
    val personality: PersonalityQueue = PersonalityQueue(),

    @NestedConfigurationProperty
    val personalityUpdate: PersonalityUpdateQueue = PersonalityUpdateQueue(),
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

data class AnalysisQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)

data class EnrichedQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)

data class PersonalityQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)

data class PersonalityUpdateQueue(
    val name: String = "",
    val exchange: String = "",
    val routingKey: String = ""
)