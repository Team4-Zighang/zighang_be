package com.zighang.core.config.rabbitmq

import com.zighang.core.clova.dto.ClovaStudioRequest
import com.zighang.core.exception.GlobalErrorCode
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class TestWorker() {

    @RabbitListener(queues = ["test.queue"])
    fun testHandleEvent(event: ClovaStudioRequest) {
        println("TestWorker: received $event")
        
        // DLQ 핸들링 테스트 용
//        throw GlobalErrorCode.INTERNAL_SERVER_ERROR.toException();
    }

    @RabbitListener(queues = ["dlq.queue"])
    fun handleErrorTest(errorEvent: Map<String, Any>) {
        println("dlq.Queue: received $errorEvent")
    }
}