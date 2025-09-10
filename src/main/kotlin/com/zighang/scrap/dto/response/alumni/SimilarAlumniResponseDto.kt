package com.zighang.scrap.dto.response.alumni

import com.zighang.jobposting.entity.value.Company
import io.swagger.v3.oas.annotations.media.Schema

data class SimilarAlumniResponseDto(

    // 동문 전공 정보 추가 필요

    @Schema(description = "동문 Id", example = "1")
    val memberId: Long,

    @Schema(description = "동문 이름", example = "멤버1")
    val memberName: String,

    @Schema(description = "동문 학교", example = "서울대학교")
    val school: String,

    @Schema(description = "동문 희망 직무", example = "개발자")
    val jobRole: List<String>,

    @Schema(description = "동문이 스크랩한 공고 회사")
    val companyLists: List<Company>,
)