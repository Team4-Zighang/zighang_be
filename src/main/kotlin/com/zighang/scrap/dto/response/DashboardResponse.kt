package com.zighang.scrap.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class DashboardResponse(
    @Schema(description = "스크랩 식별자", example = "1")
    val scrapId: Long,

    @Schema(description = "메모 식별자", example = "1")
    val memoId : Long?,

    @Schema(description = "메모 내용", example = "메모 내용 내용")
    val memoDescription: String?,

    @Schema(description = "공고 정보")
    val jobPostingResponse : JobPostingResponse,

    @Schema(description = "이력서 정보")
    val fileResponse : FileResponse,

    @Schema(description = "포트폴리오 정보")
    val portfolioResponse : FileResponse
) {
    companion object {
        fun create(scrapId: Long, memoId: Long?, memoDescription: String?, jobPostingResponse: JobPostingResponse,
                   fileResponse : FileResponse, portfolioResponse : FileResponse) : DashboardResponse{
            return DashboardResponse(
                scrapId, memoId, memoDescription, jobPostingResponse, fileResponse, portfolioResponse
            )
        }
    }
}
