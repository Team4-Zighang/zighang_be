package com.zighang.scrap.infrastructure.worker

import com.zighang.card.service.CardService
import com.zighang.core.clova.util.JsonCleaner
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.scrap.dto.request.JobEnrichedEvent
import com.zighang.scrap.dto.request.JobScrapedEvent
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import com.zighang.scrap.infrastructure.JobAnalysisCaller
import com.zighang.scrap.infrastructure.JobAnalysisEventProducer
import com.zighang.scrap.infrastructure.mapper.JobAnalysisDtoMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class AIRequestWorker(
    private val jobAnalysisEventProducer: JobAnalysisEventProducer,
    private val jobAnalysisCaller: JobAnalysisCaller,
    private val jobAnalysisDtoMapper: JobAnalysisDtoMapper,
    private val jobPostingRepository: JobPostingRepository,
    private val cardService: CardService
) {
    @RabbitListener(queues= ["\${mq.analysis.name}"])
    fun jobPostingToClova(event : JobScrapedEvent) {
        try {
            // clova 자격요건/우대사항 분석
            val result = jobAnalysisCaller.call(event.ocrData).result.message.content

            val jobPostingAnalysisDto = jobAnalysisDtoMapper.toJsonDto(JsonCleaner.cleanJson(result))

            jobAnalysisEventProducer.publishEnriched(
                JobEnrichedEvent(
                    event.id,
                    event.memberId,
                    JobPostingAnalysisDto(
                        jobPostingAnalysisDto.qualification,
                        jobPostingAnalysisDto.preferentialTreatment,
                        jobPostingAnalysisDto.career
                    ),
                    event.isCard
                )
            )
        } catch (e: Exception) {
            throw GlobalErrorCode.CLOVA_API_CALL_FAILED.toException()
        }
    }

    @RabbitListener(queues= ["\${mq.enriched.name}"])
    fun jobEnriched(event : JobEnrichedEvent) {
        try{
            val updatedRows = jobPostingRepository.updateJobPostingAnalysis(
                event.id,
                event.jobPostingAnalysisDto.qualification,
                event.jobPostingAnalysisDto.preferentialTreatment,
                event.jobPostingAnalysisDto.career
            )

            if(updatedRows == 0) {
                throw IllegalArgumentException("posting id : ${event.id}의 자격요건/우대사항 업데이트를 실패했습니다.")
            }

            if(event.isCard == true) {
                cardService.updateCardByJobPostingId(event.memberId!!, event.id, event.jobPostingAnalysisDto.career)
            }
        }catch (e:Exception){
            throw GlobalErrorCode.INTERNAL_SERVER_ERROR.toException()
        }
    }
}