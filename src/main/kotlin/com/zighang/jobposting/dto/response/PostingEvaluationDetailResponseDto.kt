package com.zighang.jobposting.dto.response

data class PostingEvaluationDetailResponseDto(

    val score: Int,

    val major: String,

    val createdAt: String,

    val recruitmentStep: String,

    val evalText: String
) {
}