package com.zighang.card.dto

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

data class CardRedis(
    var cardId : Long,
    var jobPostingId : Long,
    var cardJobPosting : CardJobPosting?,
    var isOpen : Boolean,
    var openDateTime : LocalDateTime?
) {
    companion object {
        private val idGenerator = AtomicLong(1)
        fun create(jobPostingId: Long, cardJobPosting: CardJobPosting?, isOpen: Boolean, openDateTime: LocalDateTime?)
        : CardRedis {
            return CardRedis(
                cardId = idGenerator.getAndIncrement(),
                cardJobPosting = cardJobPosting,
                jobPostingId = jobPostingId,
                isOpen = isOpen,
                openDateTime = openDateTime
            )
        }
    }
}
