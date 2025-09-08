package com.zighang.jobposting.infrastructure.worker

import com.zighang.core.clova.util.JsonCleaner
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.scrap.dto.request.JobEnrichedEvent
import com.zighang.scrap.dto.request.JobAnalysisEvent
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import com.zighang.jobposting.infrastructure.caller.JobAnalysisCaller
import com.zighang.jobposting.infrastructure.producer.JobAnalysisEventProducer
import com.zighang.jobposting.infrastructure.mapper.JobAnalysisDtoMapper
import com.zighang.jobposting.repository.JobPostingRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class JobPostingAnalysisWorker(
    private val jobAnalysisCaller: JobAnalysisCaller,
    private val jobAnalysisDtoMapper: JobAnalysisDtoMapper,
    private val jobAnalysisEventProducer: JobAnalysisEventProducer,
    private val jobPostingRepository: JobPostingRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {

    private fun tryLockJobPosting(jobPostingId: Long, ttlSeconds: Long = 180L): Boolean {
        val key = "lock:jobPosting:$jobPostingId"
        val success = redisTemplate.opsForValue().setIfAbsent(key, "locked")
        if (success == true) {
            redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds))
        }
        return success == true
    }

    private fun unlockJobPosting(jobPostingId: Long) {
        val key = "lock:jobPosting:$jobPostingId"
        redisTemplate.delete(key)
    }

    @RabbitListener(queues= ["\${mq.analysis.name}"])
    fun jobPostingToClova(event : JobAnalysisEvent) {

        var acquiredLock = false

        try {
            acquiredLock = tryLockJobPosting(event.id)

            // clova 자격요건/우대사항 분석
            // 걸어두고 오류나면 원래 코드로 바꾸기
            val jobPostingAnalysisDto = if (acquiredLock) {
                val result = jobAnalysisCaller.call(event.ocrData).result.message.content
                jobAnalysisDtoMapper.toJsonDto(JsonCleaner.cleanJson(result))
            } else {
                jobPostingRepository.findById(event.id)
                    .map { posting ->
                        JobPostingAnalysisDto(
                            qualification = posting.qualification,
                            preferentialTreatment = posting.preferentialTreatment,
                            career = posting.career
                        )
                    }.orElse(JobPostingAnalysisDto())
            }
            jobAnalysisEventProducer.publishEnriched(
                JobEnrichedEvent(
                    id = event.id,
                    memberId = event.memberId,
                    jobPostingAnalysisDto = jobPostingAnalysisDto,
                    isCard = event.isCard
                )
            )
        } catch (e: Exception) {
            throw GlobalErrorCode.CLOVA_API_CALL_FAILED.toException()
        } finally {
            if (acquiredLock) unlockJobPosting(event.id)
        }
    }
}