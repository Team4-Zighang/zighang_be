package com.zighang.core.oauth.dto

data class UserDto(
    val registrationId: String,

    val name: String,

    val username: String,

    val email: String? = null,

    val profileImage: String? = null,

    val userId: Long,
)