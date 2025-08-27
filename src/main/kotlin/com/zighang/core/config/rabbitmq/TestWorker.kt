package com.zighang.core.config.rabbitmq

import com.zighang.core.clova.dto.ClovaStudioRequest
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class TestWorker() {

    @RabbitListener(queues = ["test.queue"])
    fun testHandleEvent(event: ClovaStudioRequest) {
        println("TestWorker: received $event")
    }
}