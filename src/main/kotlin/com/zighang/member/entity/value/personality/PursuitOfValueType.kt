package com.zighang.member.entity.value.personality

enum class PursuitOfValueType(
    val displayName: String,
) {

    PERSONAL_GROWTH("개인 성장"),
    WELFARE_FEE("연봉/복지");

    companion object {
        fun fromDisplayName(name: String): PursuitOfValueType? {
            return entries.find { it.displayName == name }
        }
    }
}