package com.zighang.scrap.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.PageResponse
import com.zighang.core.presentation.RestResponse
import com.zighang.scrap.dto.response.alumni.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Alumni", description = "동문관 관련 API")
interface AlumniSwagger {

    @Operation(
        summary = "동문관 - 나와 같은 직무를 희망하는 동문이 가장 많이 스크랩한 공고 top3 보기",
        description = "나와 같은 직무를 희망하는 동문이 가장 많이 스크랩한 공고 top3를 봅니다.",
        operationId = "/alumni/similar/job-postings/top3",
    )
    fun getTop3ScrappedJobPostingsBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) : ResponseEntity<RestResponse<List<AlumniTop3JobPostingScrapResponseDto>>>

    @Operation(
        summary = "동문관 - 나와 같은 직무를 희망하는 동문에게 가장 인기있는 기업 Top3",
        description = "나와 같은 직무를 희망하는 동문에게 가장 인기있는 기업 top3를 봅니다",
        operationId = "/alumni/similar/companies/top3",
    )
    fun getTop3CompanyBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) : ResponseEntity<RestResponse<List<AlumniTop3CompanyResponseDto>>>

    @Operation(
        summary = "동문관 - 나와 같은 직무를 희망하는 동문이 스크랩한 공고 보기",
        description = "나와 같은 직무를 희망하는 동문의 스크랩한 공고를 봅니다.",
        operationId = "/alumni/similar/scraps"
    )
    fun getScrappedJobPostingsBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam page: Int,
        @RequestParam(required = false, defaultValue = "false") isMobile: Boolean,
    ): ResponseEntity<RestResponse<PageResponse<AlumniSimiliarJobPostingResponseDto>>>

    @Operation(
        summary = "동문관 - 나와 같은 직무를 희망하는 동문들의 정보를 봅니다.",
        description = "나와 같은 직무를 희망하는 동문들의 정보를 봅니다",
        operationId = "/alumni/similar/info"
    )
    fun getAlumniBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
    ) : ResponseEntity<RestResponse<List<SimilarAlumniResponseDto>>>

    @Operation(
        summary = "동문관 - 나와 같은 직무를 희망하는 동문의 스크랩 세부 정보를 봅니다.",
        description = "나와 같은 직무를 희망하는 동문들의 세부 정보를 봅니다.",
        operationId = "/alumni/similar/info/{memberId}"
    )
    fun getAlumniBySimilarUsersDetailController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable memberId: Long
    ) : ResponseEntity<RestResponse<SimilarAlumniDetailResponseDto>>
}