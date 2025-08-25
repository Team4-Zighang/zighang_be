package com.zighang.member.dto.request

import com.zighang.member.entity.value.Major
import com.zighang.member.entity.value.School
import io.swagger.v3.oas.annotations.media.Schema

data class SchoolRequest(
    @Schema(description = "학교 이름(상수형임 주의)", example = "KONKUK")
    val school: School,
    @Schema(description = "학과(상수형임 주의)", example = "COMPUTER")
    val major: Major,
)
