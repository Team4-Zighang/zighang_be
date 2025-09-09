package com.zighang.jobposting.dto.response

import org.springframework.data.domain.Slice

data class PostingEvaluationListResponseDto (

    val schoolName : String,

    val avgScore: Double,

    val totalCount: Int,

    val evalList: Slice<PostingEvaluationDetailResponseDto>
){
}