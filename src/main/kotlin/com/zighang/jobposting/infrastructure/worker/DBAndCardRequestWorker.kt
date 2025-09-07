package com.zighang.jobposting.infrastructure.worker

import com.zighang.card.service.CardService
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.scrap.dto.request.JobEnrichedEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener

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
                throw IllegalArgumentException("posting id : ${event.id}의 자격요건/우대사항 업데이트를 실패했습니다.")
            }

            if(event.isCard == true) {
                // 해당 부분에서 카드 레디스 업데이트
                // 카드 이벤트 발행의 경우에서만 실행
                cardService.updateCardByJobPostingId(event.memberId!!, event.id, event.jobPostingAnalysisDto.career)
            }
        }catch (e:Exception){
            throw GlobalErrorCode.INTERNAL_SERVER_ERROR.toException()
        }
    }
}