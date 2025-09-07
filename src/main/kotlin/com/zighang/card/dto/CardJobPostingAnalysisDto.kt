package com.zighang.card.dto

class CardJobPostingAnalysisDto (
    val career : String?,
    val recruitmentType : String?,
    val academicConditions : String?
) {
    companion object{
        fun create(career: String?, recruitmentType: String?, academicConditions: String?): CardJobPostingAnalysisDto {
            return CardJobPostingAnalysisDto(
                career = career,
                recruitmentType = recruitmentType,
                academicConditions = academicConditions
            )
        }
    }
}