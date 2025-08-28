package com.zighang.scrap.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.PageResponse
import com.zighang.core.presentation.RestResponse
import com.zighang.scrap.dto.request.UpsertScrapRequest
import com.zighang.scrap.dto.response.DashboardResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

interface ScrapSwagger {
    @Operation(
        summary = "스크랩 수행",
        description = "스크랩을 수행합니다.",
        operationId = "/scrap/{postingId}",
    )
    fun doScrap(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody upsertScrapRequest: UpsertScrapRequest
    ) : ResponseEntity<RestResponse<Boolean>>

    @Operation(
        summary = "대시보드 조회",
        description = "스크랩을 대시보드 형태로 조회합니다.",
        operationId = "/scrap/dashboard",
    )
    fun getScrap(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(name = "page", defaultValue = "1") page : Int,
        @RequestParam(name = "size", defaultValue = "10") size : Int,
    ) : ResponseEntity<PageResponse<DashboardResponse>>
}