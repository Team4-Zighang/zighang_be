package com.zighang.scrap.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class JobScrapedEvent (
    @field:NotNull
    val id: Long,

    @field:NotBlank
    val ocrData: String,
)