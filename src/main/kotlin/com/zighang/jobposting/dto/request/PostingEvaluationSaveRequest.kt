package com.zighang.jobposting.dto.request

import com.zighang.jobposting.entity.value.RecruitmentStep
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class PostingEvaluationSaveRequestDto (

    @field:NotNull
    @Schema(description = "해당 공고 id", example = "1")
    val postingId: Long,

    @field:NotNull
    @field:Min(0)
    @field:Max(5)
    @Schema(description = "해당 공고 평점", example = "3")
    val evalScore: Int,

    @Schema(description = "해당 공고 공고 평", example = "저를 안뽑아줘서 채용공고가 불친절하다 생각했어요.")
    val evalText: String,

    @field:NotNull
    @Schema(
        description = "해당 공고 합격 여부 (enumType)",
        example = "DOCUMENT_FAILED(\"서류 탈락\"), " +
                "DOCUMENT_PASSED(\"서류 합격\"), " +
                "FIRST_INTERVIEW(\"1차 면접\"), " +
                "SECOND_INTERVIEW(\"2차 면접\"), " +
                "FINAL_INTERVIEW(\"최종 면접\"), " +
                "FINAL_PASSED(\"최종 합격\")"
    )
    val recruitmentStep: RecruitmentStep,
) {
}