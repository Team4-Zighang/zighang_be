package com.zighang.core.oauth.handler

import com.zighang.core.jwt.TokenService
import com.zighang.core.oauth.CustomOAuth2User
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import com.zighang.member.entity.value.Role
import com.zighang.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class CustomOAuth2SuccessHandler(
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
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

        val existingMember = memberRepository.findByEmail(principal.getEmail())
        val userId = principal.getUserId()
        println(userId)
        val roleName = (existingMember?.role ?: Role.GUEST).name
        val accessToken = tokenService.provideAccessToken(userId, roleName)
        val refreshToken = tokenService.provideRefreshToken(userId, roleName)

        val targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
            .queryParam("accessToken", accessToken)
            .queryParam("refreshToken", refreshToken)
            .build().toUriString()

        super.getRedirectStrategy().sendRedirect(request, response, targetUrl)
    }
}