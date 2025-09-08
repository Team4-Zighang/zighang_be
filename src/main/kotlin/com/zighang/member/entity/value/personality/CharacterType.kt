package com.zighang.member.entity.value.personality

enum class CharacterType(
    val displayName: String
) {
    DEUMJIK("듬직행"),
    SILSOK("실속행"),
    SEONGSIL("성실행"),
    DANJJAN("단짠행"),
    MOHEOM("모험행"),
    JJIN_DONG("찐동행"),
    DOJEON("도전행"),
    JAYU("자유행");

    companion object {
        fun fromDisplayName(name: String): CharacterType? {
            return entries.find { it.displayName == name }
        }
    }
}