package com.zighang.card.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class CardContentResponse(
    @Schema(description = "카드 식별자", example = "1")
    val cardId : Long,
    @Schema(description = "카드 공고 정보")
    val cardJobPosting : CardJobPosting?,
    @Schema(description = "카드 최초 공개 시각")
    val cardOpenTime : LocalDateTime?
) {
    companion object {
        fun from(card: CardRedis): CardContentResponse {
            return CardContentResponse(
                cardId = card.cardId,
                cardJobPosting = card.cardJobPosting,
                cardOpenTime = card.openDateTime
            )
        }
    }
}
