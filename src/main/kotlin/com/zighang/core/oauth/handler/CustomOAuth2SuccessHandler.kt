package com.zighang.core.oauth.handler

import com.zighang.core.oauth.CustomOAuth2User
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class CustomOAuth2SuccessHandler(
    // token, repository, redirectUrl
    private val redirectUrl : String
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

        response?.sendRedirect(redirectUrl)
    }
}