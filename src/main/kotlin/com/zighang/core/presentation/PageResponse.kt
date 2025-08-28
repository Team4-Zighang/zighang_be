package com.zighang.core.presentation

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import java.time.LocalDateTime

class PageResponse<T>(
    @Schema(description = "페이지 데이터")
    val data: List<T>,

    @Schema(description = "전체 페이지 수")
    val totalPages: Int,

    @Schema(description = "마지막 페이지 여부")
    val last: Boolean,

    @Schema(description = "전체 데이터 수")
    val totalElements: Long,

    @Schema(description = "현재 페이지 (0-based)")
    val page: Int,

    @Schema(description = "페이지 크기")
    val size: Int
) : BaseResponse(
    success = true,
    timestamp = LocalDateTime.now()
) {
    companion object {
        fun <T> from(page: Page<T>): PageResponse<T> =
            PageResponse(
                data = page.content,
                totalPages = page.totalPages,
                last = page.isLast,
                totalElements = page.totalElements,
                page = page.number,
                size = page.size
            )
    }
}