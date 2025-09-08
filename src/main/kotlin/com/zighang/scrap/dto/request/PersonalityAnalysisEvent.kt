package com.zighang.scrap.dto.request

data class PersonalityAnalysisEvent(
    val memberId : Long,

    val jobCategory: String,

    val jobPostingIds: List<Long>
)