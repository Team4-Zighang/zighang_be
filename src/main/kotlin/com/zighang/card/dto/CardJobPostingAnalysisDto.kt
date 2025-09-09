package com.zighang.card.dto

class CardJobPostingAnalysisDto (
    val recruitmentType : String?,
    val academicConditions : String?
) {
    companion object{
        fun create(recruitmentType: String?, academicConditions: String?): CardJobPostingAnalysisDto {
            return CardJobPostingAnalysisDto(
                recruitmentType = recruitmentType,
                academicConditions = academicConditions
            )
        }
    }
}