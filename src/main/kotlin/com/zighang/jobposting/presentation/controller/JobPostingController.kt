package com.zighang.jobposting.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.jobposting.dto.PostingEvaluationSaveResponseDto
import com.zighang.jobposting.dto.request.PostingEvaluationSaveRequestDto
import com.zighang.jobposting.presentation.swagger.JobPostingSwagger
import com.zighang.jobposting.service.PostingEvaluationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posting")
class JobPostingController(
    private val jobPostingEvaluationService: PostingEvaluationService
) : JobPostingSwagger {

    @PostMapping("/eval")
    override fun createEvaluation(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Valid postingEvaluationSaveRequestDto: PostingEvaluationSaveRequestDto
    ): ResponseEntity<RestResponse<PostingEvaluationSaveResponseDto>> {
        return ResponseEntity.ok(
            RestResponse<PostingEvaluationSaveResponseDto>(
                jobPostingEvaluationService.saveEvaluation(
                    customUserDetails,
                    postingEvaluationSaveRequestDto
                )
            )
        )
    }


}