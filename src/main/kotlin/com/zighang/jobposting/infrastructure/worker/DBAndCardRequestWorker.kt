package com.zighang.jobposting.infrastructure.worker

import com.zighang.card.service.CardService
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.jobposting.dto.JobEnrichedEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class DBAndCardRequestWorker(
    private val jobPostingRepository: JobPostingRepository,
    private val cardService: CardService
) {
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
                throw GlobalErrorCode.NOT_EXIST_JOB_POSTING.toException()
            }

            if(event.isCard) {
                // 해당 부분에서 카드 레디스 업데이트
                val memberId = requireNotNull(event.memberId) {
                    "카드 업데이트 시 memberId가 필수로 필요합니다"
                }
                cardService.updateCardByJobPostingId(event.memberId, event.id, event.jobPostingAnalysisDto.career)
            }
        }catch (e: DomainException){
            throw e
        }
        catch (e:Exception){
            throw GlobalErrorCode.INTERNAL_SERVER_ERROR.toException()
        }
    }
}