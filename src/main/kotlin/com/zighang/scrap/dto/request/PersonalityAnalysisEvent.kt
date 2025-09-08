package com.zighang.scrap.dto.request

data class PersonalityAnalysisEvent(
    val memberId : Long,

    val jobSummaryList: List<String>
)