package com.zighang.core.presentation

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.time.LocalDateTime

@JsonPropertyOrder(*arrayOf("success", "timestamp", "data"))
class RestResponse<T>(
    val data: T
): BaseResponse(
    success = true,
    timestamp = LocalDateTime.now()
)