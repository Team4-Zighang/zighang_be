package com.zighang.scrap.infrastructure.worker

import com.zighang.scrap.dto.request.PersonalityUpdateEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PersonalityUpdateWorker {

    @RabbitListener(queues = ["\${mq.personality-update.name}"])
    fun updatePersonality(event: PersonalityUpdateEvent) {
        print(event)
    }
}