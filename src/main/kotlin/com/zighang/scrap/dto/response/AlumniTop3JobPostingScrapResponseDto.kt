package com.zighang.scrap.dto.response

import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.Company
import com.zighang.scrap.util.dDayFactory

data class AlumniTop3JobPostingScrapResponseDto(
    val postingId : Long,

    val title: String,

    val companyName: String,

    val companyImageUrl: String?,

    val depthTwo: String,

    val recruitmentType: String,

    val career: String,

    val dday: String,

    val isSaved: Boolean,
) {

    companion object {
        fun create(jobPosting: JobPosting, company: Company): AlumniTop3JobPostingScrapResponseDto {
            return AlumniTop3JobPostingScrapResponseDto(
                jobPosting.id!!,
                jobPosting.title,
                company.companyName,
                company.companyImageUrl,
                jobPosting.depthTwo,
                jobPosting.recruitmentType,
                "3년차 이상",
                dDayFactory(jobPosting),
                false
            )
        }
    }
}