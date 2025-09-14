package com.zighang.member.dto.request

import com.zighang.member.entity.value.School
import io.swagger.v3.oas.annotations.media.Schema

data class MajorRequest(
    @Schema(description = "학교명", example = "SEOUL")
    val school : School
)
