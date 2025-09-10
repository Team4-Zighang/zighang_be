package com.zighang.scrap.dto.response.alumni

import com.zighang.jobposting.entity.value.Company
import io.swagger.v3.oas.annotations.media.Schema

data class AlumniTop3CompanyResponseDto(
    @Schema(description = "회사 이름", example = "")
    val companyName: String,

    @Schema(description = "회사 이미지 url", example = "http://~")
    val companyImageUrl: String?,

    @Schema(description = "저장 여부", example = "백엔드 구현 불가 무조건 false, 해당 아이콘으로 저장도 백엔드 구현 불가능")
    val isSaved: Boolean
) {
    companion object {
        fun create(company: Company): AlumniTop3CompanyResponseDto {
            return AlumniTop3CompanyResponseDto(
                company.companyName,
                company.companyImageUrl,
                false
            )
        }
    }
}