package com.zighang.card.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CreateCardSetResponse(
    @Schema(description = "카드 식별자 리스트", example = "1")
    val cardIds: List<Long>,
) {
    companion object {
        fun create(cardIds: List<Long>): CreateCardSetResponse {
            return CreateCardSetResponse(
                cardIds
            )
        }
    }
}
