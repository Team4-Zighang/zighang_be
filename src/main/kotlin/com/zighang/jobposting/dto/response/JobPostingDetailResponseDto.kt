package com.zighang.jobposting.dto.response

import com.zighang.jobposting.entity.value.Company
import io.swagger.v3.oas.annotations.media.Schema

data class JobPostingDetailResponseDto(
    @Schema(description = "공고 식별자", example = "1")
    val postingId: Long,

    @Schema(description = "공고 제목", example = "삼성 개발자 모집중")
    val title: String,

    @Schema(description = "공고 학력 조건", example = "무관")
    val education: String,

    @Schema(description = "직무", example = "백엔드")
    val depthTwo: String?,

    @Schema(description = "경력", example = "3년차 이상")
    val career: String,

    @Schema(description = "채용 타입", example = "경력직")
    val workType: String,

    @Schema(description = "채용 지역", example = "서울")
    val region: String,

    val company: Company,

    @Schema(description = "조회수", example = "1")
    val viewCount: Int,

    @Schema(description = "채용 내용 이미지 URL", example = "http://~ (이미지 제공 안하는 경우 null)")
    val recruitmentImageUrl: String?,

    @Schema(description = "채용 내용 html 본문", example = "<h2>채용 직무</h2>\n" +
            "(위의 이미지가 null인 경우 이미지 태그에 주소를 집어 넣는 것이 아닌 해당 html을 그대로 집어넣기")
    val recruitmentContent: String?,

    @Schema(description = "원본 url", example = "원본 공고로 넘어가는 url")
    val recruitmentOriginalUrl: String?,

    @Schema(description = "게시일", example = "9월 11일 게시")
    val uploadDate: String?,

    @Schema(description = "마감일", example = "상시 or 9월 18일 23:00 마감")
    val expiredDate: String?,

    @Schema(description = "해당 공고 스크랩 여부", example = "true")
    val isSaved: Boolean,

    @Schema(description = "스크랩된 id", example = "1")
    val scrapId: Long?
) {

}
