package com.zighang.scrap.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.PageResponse
import com.zighang.core.presentation.RestResponse
import com.zighang.scrap.dto.request.ScrapDeleteRequest
import com.zighang.scrap.dto.request.UpsertScrapRequest
import com.zighang.scrap.dto.response.DashboardResponse
import com.zighang.scrap.value.FileType
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

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

    @Operation(summary = "스크랩 삭제", description = "스크랩 식별자를 통해 스크랩을 삭제합니다.")
    fun deleteScraps(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Valid request: ScrapDeleteRequest
    )

    @Operation(
        summary = "스크랩에 이력서/포트폴리오 저장",
        description = "유저가 저장한 스크랩에 이력서/포트폴리오 파일을 저장합니다.",
        operationId = "/scrap/{scrapId}/files/{fileType}",
    )
    fun fileUpload(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable scrapId: Long,
        @PathVariable fileType: FileType,
        @RequestParam("file") file: MultipartFile
    )

    @Operation(
        summary = "스크랩에 이력서/포트폴리오 삭제",
        description = "유저가 저장한 이력서/포트폴리오 파일을 삭제합니다.",
        operationId = "/scrap/{scrapId}/files/{fileType}",
    )
    fun fileDelete(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable scrapId: Long,
        @PathVariable fileType: FileType,
        @RequestParam("file") file: MultipartFile
    )
}