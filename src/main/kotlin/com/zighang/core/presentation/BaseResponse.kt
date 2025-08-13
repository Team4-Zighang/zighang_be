package com.zighang.core.presentation

import java.time.LocalDateTime

abstract class BaseResponse protected constructor(
    val success: Boolean,
    val timestamp: LocalDateTime
)