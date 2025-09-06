package com.zighang.jobposting.entity.value

enum class Education(
    val dbValue: String,
    val displayName: String
) {
    IRRELEVANT("IRRELEVANT", "무관"),
    HIGH_SCHOOL("HIGH_SCHOOL", "고등학교"),
    JUNIOR_COLLEGE("JUNIOR_COLLEGE", "전문대"),
    BACHELOR("BACHELOR", "학사"),
    MASTER("MASTER", "석사"),
    DOCTOR("DOCTOR", "박사"),
    BACHELOR_IRRELEVANT("BACHELOR,IRRELEVANT", "학사 또는 무관"),
    BACHELOR_JUNIOR_COLLEGE("BACHELOR,JUNIOR_COLLEGE", "학사 또는 전문대");

    companion object {
        fun fromDbValue(value: String): Education =
            entries.firstOrNull { it.dbValue == value }
                ?: throw IllegalArgumentException("Unknown education: $value")
    }
}