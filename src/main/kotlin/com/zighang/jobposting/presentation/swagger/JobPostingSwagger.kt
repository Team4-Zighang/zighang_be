package com.zighang.jobposting.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.jobposting.dto.PostingEvaluationSaveResponseDto
import com.zighang.jobposting.dto.request.PostingEvaluationSaveRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody

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
}