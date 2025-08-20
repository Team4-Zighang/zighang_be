package com.zighang.core.oauth.dto

import com.zighang.core.oauth.exception.OAuth2ErrorCode

class KaKaoResponse(private val attributes : Map<String, Any>) : OAuth2Response {
    override fun getProviderId(): String {
        return attributes["id"].toString()
    }

    override fun getEmail(): String {
        val kakaoAccount = attributes["kakao_account"] as? Map<*, *>

        return kakaoAccount?.get("email")?.toString()
            ?: throw OAuth2ErrorCode.CANNOT_FIND_EMAIL_IN_KAKAO.toException();
    }

    override fun getName(): String {
        val properties = attributes["properties"] as? Map<*, *>
        properties?.get("nickname")?.let {
            return it.toString()
        }

        val kakaoAccount = attributes["kakao_account"] as? Map<*, *>
        val profile = kakaoAccount?.get("profile") as? Map<*, *>
        return profile?.get("nickname")?.toString()
            ?: throw OAuth2ErrorCode.CANNOT_FIND_NICKNAME_IN_KAKAO.toException();
    }

    fun getProfileImage(): String? {
        val kakaoAccount = attributes["kakao_account"] as? Map<*, *>
        val profile = kakaoAccount?.get("profile") as? Map<*, *>
        return profile?.get("profile_image_url") as? String
    }
}
