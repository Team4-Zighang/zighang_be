package com.zighang.scrap.infrastructure.worker

import com.zighang.core.clova.infrastructure.ClovaChatCompletionCaller
import com.zighang.core.clova.infrastructure.ClovaResponseMapper
import com.zighang.core.clova.util.JsonCleaner
import com.zighang.jobposting.service.JobPostingService
import com.zighang.scrap.dto.request.PersonalityAnalysisEvent
import com.zighang.scrap.dto.request.PersonalityUpdateEvent
import com.zighang.scrap.infrastructure.PersonalityAnalysisEventProducer
import com.zighang.scrap.infrastructure.mapper.PersonalityValueDtoMapper
import com.zighang.scrap.util.SystemMessageFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PersonalityAnalysisWorker(
    private val chatCompletionCaller: ClovaChatCompletionCaller,
    private val personalityAnalysisEventProducer: PersonalityAnalysisEventProducer,
    private val clovaResponseMapper: ClovaResponseMapper,
    private val personalityValueDtoMapper: PersonalityValueDtoMapper,
    private val jobPostingService: JobPostingService
) {

    @RabbitListener(queues = ["\${mq.personality.name}"])
    fun analysisPersonality(event: PersonalityAnalysisEvent) {
        val systemMessage = SystemMessageFactory.personalityAnalysisMessageFactory()

        val userMessage = jobPostingService.getJobPostingSummaryByJobCategory(
            event.memberId, event.jobCategory, event.jobPostingIds
        )

        val response = clovaResponseMapper.toJsonDto(
            chatCompletionCaller.clovaChatCompletionApiCaller(
                systemMessage,
                userMessage,
            )
        ).result.message.content

        val personalityValueDto = personalityValueDtoMapper.toDto(
            JsonCleaner.cleanJson(response)
        )

        personalityAnalysisEventProducer.publishPersonalityUpdate(
            PersonalityUpdateEvent(
                event.memberId,
                personalityValueDto,
            )
        )
    }
}