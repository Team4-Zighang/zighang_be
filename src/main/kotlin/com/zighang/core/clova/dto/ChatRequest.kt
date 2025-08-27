package com.zighang.core.clova.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ChatRequest (

    @Schema(example = "너는 비서다")
    val systemMessage: String,

    @Schema(example = "안녕")
    val userMessage: String,
)