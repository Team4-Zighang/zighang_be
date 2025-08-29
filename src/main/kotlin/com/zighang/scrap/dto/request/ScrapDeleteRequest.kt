package com.zighang.scrap.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty

data class ScrapDeleteRequest(
    @Schema(description = "삭제할 스크랩 식별자 리스트", example = "[1, 2, 3]")
    @field:NotEmpty(message = "삭제할 스크랩 ID는 비어있을 수 없습니다.")
    val idList: List<Long>
)