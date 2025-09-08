package com.zighang.member.entity.value.personality

enum class CompanySize(
    val displayName: String,
) {
    START_UP("스타트업"),
    MAJOR_COMPANY("대기업");

    companion object{
        fun fromDisplayName(name: String): CompanySize {
            return entries.find { it.name == name }!!
        }
    }
}