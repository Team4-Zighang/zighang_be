package com.zighang.scrap.dto.response.alumni

import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.Company
import com.zighang.jobposting.entity.value.RankChange
import com.zighang.jobposting.util.getCareer
import com.zighang.scrap.util.dDayFactory
import io.swagger.v3.oas.annotations.media.Schema

data class AlumniTop3JobPostingScrapResponseDto(
    @Schema(description = "공고 식별자", example = "1")
    val postingId : Long,

    @Schema(description = "공고 제목", example = "카카오 공채")
    val title: String,

    @Schema(description = "회사 이름", example = "카카오")
    val companyName: String,

    @Schema(description = "회사 로고", example = "http://~ 오류나도 그냥 연결 ㄱㄱ 나중에 수정 예정")
    val companyImageUrl: String?,

    @Schema(description = "직무", example = "백엔드")
    val depthTwo: String,

    @Schema(description = "채용 정보", example = "정규직")
    val recruitmentType: String,

    @Schema(description = "경력", example = "해당 필드 추후 수정 예정(4-10년차)")
    val career: String,

    @Schema(description = "마감일까지 남은 dday", example = "D-7(null일 수도 있는데 추후 DB 값 조정으로 수정 예정)")
    val dday: String,

    @Schema(description = "내가 스크랩한 공고인가 여부", example = "true")
    val isSaved: Boolean,

    @Schema(description = "전일 대비 순위 등락 값", example = "1")
    val changeRankValue: Int,

    @Schema(description = "전일 대비 순위 변화", example = "STABLE")
    val changeRankStatus: String?,
) {

    companion object {
        fun create(jobPosting: JobPosting, company: Company, isSaved: Boolean): AlumniTop3JobPostingScrapResponseDto {
            return AlumniTop3JobPostingScrapResponseDto(
                jobPosting.id!!,
                jobPosting.title,
                company.companyName,
                company.companyImageUrl,
                jobPosting.depthTwo,
                jobPosting.recruitmentType,
                getCareer(jobPosting),
                dDayFactory(jobPosting),
                isSaved = isSaved,
                jobPosting.currentRank - jobPosting.lastRank,
                changeRankStatus =
                    if (jobPosting.lastRank == 0) RankChange.NEW.name else jobPosting.rankChange.name
            )
        }
    }
}