package com.zighang.jobposting.dto.response

import io.swagger.v3.oas.annotations.media.Schema

class PostingEvaluationSaveResponseDto(

    @Schema(description = "저장 공고평 id", example = "1")
    val evaluationId: Long,

    @Schema(description = "공고 식별자 id", example = "1")
    val postingId: Long,

    @Schema(description = "저장 성공 여부", example = "true")
    val isSaved: Boolean,

    @Schema(description = "저장 성공/실패 여부 메시지", example = "저장에 성공했습니다.")
    val message: String
) {
    companion object{
        fun successCreate(evaluationId: Long, postingId: Long): PostingEvaluationSaveResponseDto {
            return PostingEvaluationSaveResponseDto(
                evaluationId,
                postingId,
                true,
                "저장에 성공했습니다."
            )
        }
    }
}