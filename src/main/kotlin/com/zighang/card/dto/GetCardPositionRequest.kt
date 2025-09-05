package com.zighang.card.dto

import com.zighang.card.value.CardPosition
import io.swagger.v3.oas.annotations.media.Schema

data class GetCardPositionRequest(
    @Schema(description = "카드 위치", example = "LEFT / CENTER / RIGHT")
    val position : CardPosition
)
