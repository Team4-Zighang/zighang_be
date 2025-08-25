package com.zighang.member.entity.value

enum class Major(
    val displayName: String
) {
    //Todo 필요시 추가
    COMPUTER("컴퓨터관련 전공"),
    SEMICONDUCTOR("반도체 관련 전공"),
    CULTURAL_CONTENTS("문화컨텐츠 관련 전공"),
    ELECTRONIC("전기,전자 관련 전공"),
    MECHANICAL("기계관련 전공")
}