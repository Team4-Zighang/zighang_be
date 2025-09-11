package com.zighang.scrap.dto.response

import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.Company
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class JobPostingResponse(
    @Schema(description = "공고 id", example = "1")
    val postingId : Long,
    @Schema(description = "공고 타이틀", example = "OOO 공고")
    val title : String?,
    @Schema(description = "기업 이름", example = "(주)삼성")
    val companyName: String,
    @Schema(description = "서류 마감일", example = "2025-07-20 00:05:00.000000")
    val expiredDate : LocalDateTime?,
    @Schema(description = "D-DAY", example = "3")
    val dDay : Long?,
    @Schema(description = "자격요건", example = "2년이상의 프론트 실무 경력 보유자")
    val qualification : String?,
    @Schema(description = "우대사항", example = "git을 활용한 형상관리 경험")
    val preferentialTreatment : String?
) {
    companion object{
        fun create(
            jobPosting: JobPosting, company: Company
        ) : JobPostingResponse {
            return JobPostingResponse(
                jobPosting.id!!,
                jobPosting.title,
                company.companyName,
                jobPosting.expiredDate,
                computeDday(jobPosting.expiredDate),
                jobPosting.qualification,
                jobPosting.preferentialTreatment
            )
        }

        private fun computeDday(date : LocalDateTime?) : Long?{
            if (date == null) return null
            val today = LocalDate.now()
            val targetDate = date.toLocalDate()
            return ChronoUnit.DAYS.between(today, targetDate)
        }
    }


}
