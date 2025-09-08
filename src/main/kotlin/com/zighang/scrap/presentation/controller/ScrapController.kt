package com.zighang.scrap.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.PageResponse
import com.zighang.core.presentation.RestResponse
import com.zighang.scrap.dto.request.ScrapDeleteRequest
import com.zighang.scrap.dto.request.UpsertScrapRequest
import com.zighang.scrap.dto.response.DashboardResponse
import com.zighang.scrap.dto.response.FileDeleteResponse
import com.zighang.scrap.dto.response.FileResponse
import com.zighang.scrap.dto.response.PersonalityAnalysisDto
import com.zighang.scrap.presentation.swagger.ScrapSwagger
import com.zighang.scrap.service.PersonalityAnalysisService
import com.zighang.scrap.service.ScrapService
import com.zighang.scrap.value.FileType
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/scrap")
@Tag(name = "Scrap", description = "스크랩 관련 API")
class ScrapController(
    private val scrapService: ScrapService,
    private val personalityAnalysisService: PersonalityAnalysisService
) : ScrapSwagger{

    @PostMapping
    override fun doScrap(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody upsertScrapRequest: UpsertScrapRequest
    ): ResponseEntity<RestResponse<Boolean>> {
        scrapService.upsert(customUserDetails, upsertScrapRequest)
        return ResponseEntity.ok(
            RestResponse<Boolean>(
                true
            )
        )
    }

    @GetMapping
    override fun getScrap(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam(defaultValue = "1", name = "page") page : Int,
        @RequestParam(defaultValue = "10", name = "size") size : Int
    )
    : ResponseEntity<PageResponse<DashboardResponse>> {
        val safePage = if (page < 1) 0 else page - 1
        return ResponseEntity.ok(
            PageResponse.from(scrapService.getScrap(safePage, size, customUserDetails))
        )
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun deleteScraps(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Valid request: ScrapDeleteRequest
    ) {
        scrapService.scrapDeleteService(customUserDetails, request.idList)
    }

    @PostMapping(value = ["/{scrapId}/file/{fileType}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    override fun fileUpload(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable scrapId: Long,
        @PathVariable fileType: FileType,
        @RequestPart(name = "file") file: MultipartFile
    ): ResponseEntity<RestResponse<FileResponse>> {
        return ResponseEntity.ok(
            RestResponse<FileResponse>(
                scrapService.uploadFile(customUserDetails, scrapId, file, fileType)
            )
        )
    }

    @DeleteMapping("/{scrapId}/file/{fileType}")
    override fun fileDelete(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable scrapId: Long,
        @PathVariable fileType: FileType,
        @RequestParam fileUrl: String
    ) : ResponseEntity<RestResponse<FileDeleteResponse>>{
        return ResponseEntity.ok(
            RestResponse<FileDeleteResponse>(
                scrapService.deleteFile(customUserDetails, scrapId, fileUrl, fileType)
            )
        )
    }

    @GetMapping("/personality")
    override fun getPersonalityOfMember(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<RestResponse<PersonalityAnalysisDto>> {
        return ResponseEntity.ok(
            RestResponse<PersonalityAnalysisDto>(
                personalityAnalysisService.getPersonalityAnalysis(customUserDetails)
            )
        )
    }

}