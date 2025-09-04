package com.zighang.jobposting.entity.value

import io.swagger.v3.oas.annotations.media.Schema

data class Company(
// {'recruiterUserId': None, 'companyImageUrl': None, 'companyRegion': None, 'companyType': 'ETC',
// 'recruiterEmail': None, 'companyName': '다존산업', 'companyDescription': None, 'companyAddress': None, 'businessNumber': None}
    @Schema(description = "회사 이름", example = "다존산업")
    val companyName: String,

    @Schema(description = "회사 로고 이미지 url", example = "https://~")
    val companyImageUrl: String?,
)