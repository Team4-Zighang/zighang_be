package com.zighang.core.infrastructure.filter

import com.zighang.core.infrastructure.CustomUserDetailsService
import com.zighang.core.jwt.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenService: TokenService,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    companion object {
        private const val AT_IN_COOKIE = "access_token"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val excludedPaths = listOf(
            "/v1/auth", "/v1/oauth", "/swagger", "/v3/api-docs", "/api-docs"
        )
        return excludedPaths.any { path ->
            request.requestURI.startsWith(path) || request.requestURI == path
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val accessToken = getCookieValue(request, AT_IN_COOKIE) ?: getAccessTokenFromHeader(request)

        when {
            accessToken != null && tokenService.validateToken(accessToken) -> {
                applyAuthentication(accessToken)
            }

            else -> {
                // TODO: 에러 응답 처리
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun applyAuthentication(token: String) {
        val userId = tokenService.getMemberIdFromToken(token)
        val userDetails = customUserDetailsService.loadUserById(userId)

        val auth = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.authorities
        )
        SecurityContextHolder.getContext().authentication = auth
    }

    private fun getCookieValue(request: HttpServletRequest, name: String): String? {
        return request.cookies?.firstOrNull { it.name == name }?.value
    }

    private fun getAccessTokenFromHeader(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER)
        return if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            authorizationHeader.substring(7)
        } else {
            null
        }
    }
}
