package com.zighang.scrap.dto.request

data class PersonalityUpdateEvent(
    val memberId : Long,

    val personalityValue : PersonalityValueDto
)