package com.zighang.card.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CardJobPosting(
    @Schema(description = "공고 Id", example = "공고 ID")
    val jobPostingId : Long,
    @Schema(description = "기업 이미지 url", example = "이미지 url")
    val companyImageUrl : String?,
    @Schema(description = "기업 이름", example = "농협은행")
    val companyName : String?,
    @Schema(description = "공고 제목", example = "[NH 농협은행] 경영지원부 기술 업무 전문인력 모집")
    val title : String?,
    @Schema(description = "경력 정보", example = "4 ~ 10년차")
    val career : String?,
    @Schema(description = "채용 유형", example = "계약직")
    val recruitmentType : String?,
    @Schema(description = "학력 조건", example = "학력 무관")
    val academicConditions : String?,
    @Schema(description = "지역", example = "서울")
    val address : String?,
    @Schema(description = "스크랩 여부", example = "true")
    var isScrap : Boolean,
    @Schema(description = "스크랩 아이디", example = "1")
    var scrapId: Long?
) {
    companion object {
        fun create(
            jobPostingId: Long,
            companyImageUrl: String?,
            companyName: String?,
            title: String?,
            career: String?,
            recruitmentType: String?,
            academicConditions: String?,
            address: String?,
            isScrap: Boolean,
            scrapId : Long?
        ) : CardJobPosting{
            return CardJobPosting(
                jobPostingId, companyImageUrl, companyName, title, career, recruitmentType, academicConditions, address, isScrap, scrapId
            )
        }
    }
}
