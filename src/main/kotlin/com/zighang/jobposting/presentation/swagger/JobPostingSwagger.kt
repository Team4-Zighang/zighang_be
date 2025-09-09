package com.zighang.jobposting.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.jobposting.dto.response.PostingEvaluationSaveResponseDto
import com.zighang.jobposting.dto.request.PostingEvaluationSaveRequestDto
import com.zighang.jobposting.dto.response.PostingEvaluationListResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Tag(name="Job-Posting", description = "공고 상세 API")
interface JobPostingSwagger {

    @Operation(
        summary = "공고 상세 - 공고평 저장하기",
        description = "공고평을 작성하여 저장합니다.",
        operationId = "/posting/eval"
    )
    fun createEvaluation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Valid postingEvaluationSaveRequestDto: PostingEvaluationSaveRequestDto
    ): ResponseEntity<RestResponse<PostingEvaluationSaveResponseDto>>

    @Operation(
        summary = "공고 상세 - 공고평 리스트 불러오기",
        description = "공고평을 불러옵니다. 무한스크롤로 동작합니다.(hasNext, totalCount 존재), default pageSize는 10입니다.",
        operationId = "/posting/eval/{postingId}"
    )
    fun getEvaluationList(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable postingId: Long,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
    ): ResponseEntity<RestResponse<PostingEvaluationListResponseDto>>
}