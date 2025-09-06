package com.zighang.card.dto

data class RemainScrapResponse(
    var scrapCount : Long
) {
    companion object {
        fun create(scrapCount: Long) : RemainScrapResponse {
            return RemainScrapResponse(
                scrapCount =  scrapCount
            );
        }
    }
}
