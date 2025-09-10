package com.zighang.scrap.dto.response.alumni

import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.Company
import com.zighang.member.entity.value.Region
import com.zighang.scrap.util.dDayFactory
import io.swagger.v3.oas.annotations.media.Schema

data class AlumniSimiliarJobPostingResponseDto(

    @Schema(description = "공고 식별자", example = "1")
    val postingId: Long? = null,

    @Schema(description = "공고 제목", example = "제목")
    val postingTitle: String,

    @Schema(description = "회사 이름", example = "삼성")
    val companyName: String?,

    @Schema(description = "회사 로고 이미지", example = "http://~ (추후 이미지 뜨게 수정 예정)")
    val companyImageUrl: String?,

    // 경력 정보 저장 여부 확인 후 수정
    @Schema(description = "경력 정보", example = "3~10년(추후 로직 수정 예정 그냥 연결 ㄱㄱ)")
    val career: String,

    @Schema(description = "채용 타입", example = "경력직")
    val recruitmentType: String,

    @Schema(description = "학력 요건", example = "석사 이상")
    val education: String,

    // 지역정보 확인 후 수정
    @Schema(description = "채용 지역", example = "서울 (영어로 뜰텐데 변환 코드가 다른 브랜치에 있어서 일단 연결 ㄱㄱ)")
    val region: String,

    // 조회수 필드 추가시 수정
    @Schema(description = "조회수", example = "1")
    val totalViews: Int,

    @Schema(description = "디데이", example = "현재는 null, 추후 수정")
    // D-day
    val dday: String,

    // 내가 저장한 여부
    @Schema(description = "저장 여부", example = "false")
    val isSaved: Boolean
) {
    companion object {
        fun create(jobPosting: JobPosting, company: Company, isSaved: Boolean): AlumniSimiliarJobPostingResponseDto {
            return AlumniSimiliarJobPostingResponseDto(
                jobPosting.id,
                jobPosting.title,
                company.companyName,
                company.companyImageUrl,
                "경력 3 ~ 10년",
                jobPosting.recruitmentType,
                jobPosting.education.displayName,
                jobPosting.recruitmentRegion,
                jobPosting.viewCount,
                dDayFactory(jobPosting),
                isSaved = isSaved,
            )
        }
    }
}
