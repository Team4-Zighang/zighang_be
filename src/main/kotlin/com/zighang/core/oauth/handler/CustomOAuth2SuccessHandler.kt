package com.zighang.core.oauth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.jwt.TokenService
import com.zighang.core.oauth.CustomOAuth2User
import com.zighang.core.oauth.dto.TokenResponseDto
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import com.zighang.member.repository.MemberRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomOAuth2SuccessHandler(
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository,
    @Value("\${oauth2.redirect-url}")
    private val redirectUrl: String,
    private val objectMapper: ObjectMapper
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

        val userId = principal.getUserId()
        val role = principal.authorities.firstOrNull()?.authority
        val accessToken = tokenService.provideAccessToken(userId, role ?: "GUEST")
        val refreshToken = tokenService.provideRefreshToken(userId, role ?: "GUEST")

        val existingMember = memberRepository.findByEmail(principal.getEmail())

        val tokenDto = existingMember?.let {
            TokenResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
                name = it.name,
                role = it.role,
            )
        }

        response?.apply {
            status = HttpStatus.OK.value()
            contentType = MediaType.APPLICATION_JSON_VALUE
            characterEncoding = Charsets.UTF_8.name()
            writer.write(objectMapper.writeValueAsString(tokenDto))
        }
    }
}