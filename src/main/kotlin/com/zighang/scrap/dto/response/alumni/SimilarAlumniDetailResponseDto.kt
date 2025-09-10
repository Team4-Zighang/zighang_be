package com.zighang.scrap.dto.response.alumni

import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import io.swagger.v3.oas.annotations.media.Schema

data class SimilarAlumniDetailResponseDto(

    @Schema(description = "멤버 식별자", example = "1")
    val memberId: Long,

    @Schema(description = "멤버 이름", example = "김한국")
    val memberName: String,

    @Schema(description = "전공", example = "컴퓨터공학과")
    val major: String,

    @Schema(description = "직무", example = "백엔드")
    val jobRole: String,

    val scrapList: List<AlumniSimiliarJobPostingResponseDto>
){
    companion object {
        fun create(
            member: Member,
            onboarding: Onboarding,
            jobRole: String,
            list: List<AlumniSimiliarJobPostingResponseDto>
        ) : SimilarAlumniDetailResponseDto{
            return SimilarAlumniDetailResponseDto(
                member.id,
                member.name,
                onboarding.major,
                jobRole,
                list
            )
        }
    }
}