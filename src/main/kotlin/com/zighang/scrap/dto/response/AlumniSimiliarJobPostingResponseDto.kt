package com.zighang.scrap.dto.response

import com.zighang.jobposting.entity.JobPosting
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class AlumniSimiliarJobPostingResponseDto(

    val postingId: Long? = null,

    val postingTitle: String,

    val companyName: String,

    val companyImageUrl: String,

    // 경력 정보 저장 여부 확인 후 수정
    val career: String,

    val recruitmentType: String,

    val education: String,

    // 지역정보 확인 후 수정
    val region: String,

    // 조회수 필드 추가시 수정
    val totalViews: Int,

    // D-day
    val dday: String,

    // 내가 저장한 여부
    val isSaved: Boolean
) {
    companion object {
        fun create(jobPosting: JobPosting): AlumniSimiliarJobPostingResponseDto {
            val dday: String = if (jobPosting.recruitmentEndDate == null) {
                "상시"
            } else if(jobPosting.recruitmentEndDate.isAfter(LocalDateTime.now())) {
                val ddayCount = ChronoUnit.DAYS.between(LocalDateTime.now(), jobPosting.recruitmentEndDate)
                "D-$ddayCount"
            } else {
                "마감"
            }
            return AlumniSimiliarJobPostingResponseDto(
                jobPosting.id,
                jobPosting.title,
                jobPosting.company,
                jobPosting.company,
                "경력 3 ~ 10년",
                jobPosting.recruitmentType,
                jobPosting.education,
                jobPosting.recruitmentRegion,
                0,
                dday,
                false,
            )
        }
    }
}
