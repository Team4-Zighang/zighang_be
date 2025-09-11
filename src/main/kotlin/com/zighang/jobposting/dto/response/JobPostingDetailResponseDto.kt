package com.zighang.jobposting.dto.response

import com.zighang.jobposting.entity.value.Company

data class JobPostingDetailResponseDto(
    val postingId: Long,
    val title: String,
    val education: String,
    val depthTwo: String?,
    val career: String,
    val workType: String,
    val region: String,
    val company: Company,
    val viewCount: Int,
    val recruitmentImageUrl: String?,
    val recruitmentContent: String?
) {

}
