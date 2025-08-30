package com.zighang.core.config.rabbitmq

import com.zighang.core.clova.dto.ClovaStudioRequest
import com.zighang.core.config.rabbitmq.config.RabbitProperties
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class TestWorker(
    private val rabbitProperties: RabbitProperties
) {

    @RabbitListener(queues = ["\${mq.test.name}"])
    fun testHandleEvent(event: ClovaStudioRequest) {
        println("TestWorker: received $event")
        
        // DLQ 핸들링 테스트 용
//        throw GlobalErrorCode.INTERNAL_SERVER_ERROR.toException();
    }

    @RabbitListener(queues = ["\${mq.dlq.name}"])
    fun handleErrorTest(errorEvent: Map<String, Any>) {
        println("dlq.Queue: received $errorEvent")
    }
}