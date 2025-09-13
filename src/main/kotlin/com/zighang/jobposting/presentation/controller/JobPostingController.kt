package com.zighang.jobposting.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.jobposting.dto.response.PostingEvaluationSaveResponseDto
import com.zighang.jobposting.dto.request.PostingEvaluationSaveRequestDto
import com.zighang.jobposting.dto.response.JobPostingDetailResponseDto
import com.zighang.jobposting.dto.response.PostingEvaluationListResponseDto
import com.zighang.jobposting.presentation.swagger.JobPostingSwagger
import com.zighang.jobposting.service.JobPostingService
import com.zighang.jobposting.service.PostingEvaluationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/posting")
class JobPostingController(
    private val jobPostingEvaluationService: PostingEvaluationService,
    private val jobPostingService: JobPostingService
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

    @GetMapping("/eval/{postingId}")
    override fun getEvaluationList(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable postingId: Long,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
    ): ResponseEntity<RestResponse<PostingEvaluationListResponseDto>> {
        val safePage = if (page < 0) 0 else page
        return ResponseEntity.ok(
            RestResponse<PostingEvaluationListResponseDto>(
                jobPostingEvaluationService.getEvaluationList(
                    customUserDetails,
                    postingId,
                    safePage
                )
            )
        )
    }

    @GetMapping("{postingId}")
    fun getDetailOfPosting(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails?,
        @PathVariable postingId: Long,
    ) : ResponseEntity<RestResponse<JobPostingDetailResponseDto>> {
        return ResponseEntity.ok(
            RestResponse<JobPostingDetailResponseDto>(
                jobPostingService.getOneJobPosting(postingId, customUserDetails)
            )
        )
    }
}