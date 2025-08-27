package com.zighang.core.clova.dto

data class ClovaStudioRequest(
    val messages: List<ClovaMessage>,

    val maxTokens: Int,
)