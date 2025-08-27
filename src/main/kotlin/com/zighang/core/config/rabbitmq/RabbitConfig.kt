package com.zighang.core.config.rabbitmq

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter()
        return template
    }

    @Bean
    fun testQueue(): Queue = Queue("test.queue", true)

    @Bean
    fun exchange(): DirectExchange = DirectExchange("test.exchange")

    @Bean
    fun testBinding(): Binding =
        BindingBuilder.bind(testQueue()).to(exchange()).with("test.queue")
}
