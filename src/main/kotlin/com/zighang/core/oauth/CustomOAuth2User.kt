package com.zighang.core.oauth

import com.zighang.core.oauth.dto.UserDto
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val userDto: UserDto
) : OAuth2User {
    override fun getName(): String {
        return userDto.name
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return mutableMapOf()
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    fun getEmail(): String {
        return userDto.email ?: "";
    }

    fun getUserId(): Long {
        return userDto.userId
    }
}