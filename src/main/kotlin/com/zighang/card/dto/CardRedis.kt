package com.zighang.card.dto

import com.zighang.card.value.CardPosition
import java.time.LocalDateTime

data class CardRedis(
    var jobPostingId : Long,
    var cardJobPosting : CardJobPosting?,
    var isOpen : Boolean,
    var openDateTime : LocalDateTime?,
    var position : CardPosition?
) {
    companion object {
        fun create(jobPostingId: Long, cardJobPosting: CardJobPosting?, isOpen: Boolean, openDateTime: LocalDateTime?)
        : CardRedis {
            return CardRedis(
                cardJobPosting = cardJobPosting,
                jobPostingId = jobPostingId,
                isOpen = isOpen,
                openDateTime = openDateTime,
                position = null
            )
        }
    }
}
