package com.zighang.scrap.dto.response.alumni

import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding

data class SimilarAlumniDetailResponseDto(

    val memberId: Long,

    val memberName: String,

    val major: String,

    val jobRole: String,

    val scrapList: List<AlumniSimiliarJobPostingResponseDto>
){
    companion object {
        fun create(member: Member, onboarding: Onboarding, list: List<AlumniSimiliarJobPostingResponseDto>): SimilarAlumniDetailResponseDto{
            return SimilarAlumniDetailResponseDto(
                member.id,
                member.name,
                onboarding.major,
                onboarding.jobRole,
                list
            )
        }
    }
}