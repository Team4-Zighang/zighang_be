package com.zighang.member.entity.value.personality

enum class WorkType(
    val displayName: String,
) {
    OFFICE("오피스"),
    REMOTE("원격/탄력");

    companion object{
        fun fromDisplayName(name: String): WorkType?{
            return entries.find { it.name == name }
        }
    }
}