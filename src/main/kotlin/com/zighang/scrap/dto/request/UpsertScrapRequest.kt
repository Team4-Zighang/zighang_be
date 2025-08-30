package com.zighang.scrap.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class UpsertScrapRequest(
    @Schema(description = "스크랩 식별자(만약 새로 만드는 단계면 null값으로)", example = "1")
    val scrapId : Long?,
    @Schema(description = "공고 식별자", example = "1")
    val jobPostingId : Long,
)
