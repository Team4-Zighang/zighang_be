package com.zighang.jobposting.entity.value

enum class RecruitmentStep(
    val displayValue: String
) {
    DOCUMENT_FAILED("서류 탈락"),
    DOCUMENT_PASSED("서류 합격"),
    FIRST_INTERVIEW("1차 면접"),
    SECOND_INTERVIEW("2차 면접"),
    FINAL_INTERVIEW("최종 면접"),
    FINAL_PASSED("최종 합격");

    companion object{
        fun fromDisplayValue(value: String): RecruitmentStep? {
            return entries.find { it.displayValue == value}
        }
    }
}