package com.zighang.memo.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class MemoCreateRequest(

    @Schema(example = "1")
    val postingId: Long,

    @Schema(example = "메모 저장 저장")
    val content: String,
)