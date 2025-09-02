package com.zighang.scrap.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.PageResponse
import com.zighang.core.presentation.RestResponse
import com.zighang.scrap.dto.response.AlumniSimiliarJobPostingResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Alumni", description = "동문관 관련 API")
interface AlumniSwagger {

    @Operation(
        summary = "동문관 - 나와 같은 직무를 희망하는 동문이 스크랩한 공고 보기",
        description = "나와 같은 직무를 희망하는 동문의 스크랩한 공고를 봅니다.",
        operationId = "/alumni/scrap/list"
    )
    fun getScrappedJobPostingsBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam page: Int,
    ): ResponseEntity<RestResponse<PageResponse<AlumniSimiliarJobPostingResponseDto>>>
}