package com.zighang.scrap.dto.response

import com.zighang.jobposting.entity.value.Company

data class AlumniTop3CompanyResponseDto(
    val companyName: String,

    val companyImageUrl: String?,

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