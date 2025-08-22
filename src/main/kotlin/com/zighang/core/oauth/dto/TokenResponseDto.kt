package com.zighang.core.oauth.dto

class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val name: String,
    val role: String,
) {
}