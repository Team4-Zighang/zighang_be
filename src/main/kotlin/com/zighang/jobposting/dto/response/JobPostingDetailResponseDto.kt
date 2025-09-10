package com.zighang.jobposting.dto.response

import com.zighang.jobposting.entity.value.Company

data class JobPostingDetailResponseDto(
    val education : String,
    val depthTwo : String,
    val career : String,
    val workType : String,
    val region : String,
    val company: Company,
    val viewCount : Int,
) {

}
