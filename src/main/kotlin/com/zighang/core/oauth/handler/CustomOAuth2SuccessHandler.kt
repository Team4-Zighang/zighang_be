package com.zighang.core.oauth.handler

import com.zighang.core.oauth.CustomOAuth2User
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomOAuth2SuccessHandler(
    // token, repository, redirectUrl
    @Value("\${oauth2.redirect-url}")
    private val redirectUrl: String,
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication
    ) {
        val principal = authentication.principal

        if(principal !is CustomOAuth2User) {
            throw OAuth2ErrorCode.IS_NOT_OAUTH2_USER.toException()
        }

        // token 설정 해주기
    }
}