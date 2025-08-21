package com.zighang.core.oauth.dto

import com.zighang.member.entity.Role

class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val name: String,
    val role: Role
) {
}