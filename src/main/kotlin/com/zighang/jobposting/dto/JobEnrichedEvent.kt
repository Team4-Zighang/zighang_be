package com.zighang.jobposting.dto

import com.zighang.scrap.dto.response.JobPostingAnalysisDto

data class JobEnrichedEvent(

    val id : Long,

    val memberId: Long? = null,

    val jobPostingAnalysisDto: JobPostingAnalysisDto,

    val isCard: Boolean? = false
)