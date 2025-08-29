package com.zighang.scrap.dto.request

import com.zighang.scrap.dto.response.JobPostingAnalysisDto

data class JobEnrichedEvent(

    val id : Long = 0L,

    val jobPostingAnalysisDto: JobPostingAnalysisDto,
)