package com.zighang.scrap.dto.response.alumni

import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.Company
import com.zighang.scrap.util.dDayFactory

data class AlumniSimiliarJobPostingResponseDto(

    val postingId: Long? = null,

    val postingTitle: String,

    val companyName: String?,

    val companyImageUrl: String?,

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
        fun create(jobPosting: JobPosting, company: Company): AlumniSimiliarJobPostingResponseDto {
            return AlumniSimiliarJobPostingResponseDto(
                jobPosting.id,
                jobPosting.title,
                company.companyName,
                company.companyImageUrl,
                "경력 3 ~ 10년",
                jobPosting.recruitmentType,
                jobPosting.education,
                jobPosting.recruitmentRegion,
                0,
                dDayFactory(jobPosting),
                false,
            )
        }
    }
}
