package com.zighang.core.config.security

import com.zighang.core.infrastructure.filter.JwtAuthenticationFilter
import com.zighang.core.oauth.handler.CustomOAuth2AccessDeniedHandler
import com.zighang.core.oauth.handler.CustomOAuth2SuccessHandler
import com.zighang.core.oauth.service.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customOAuth2SuccessHandler: CustomOAuth2SuccessHandler,
    private val customOAuth2AccessDeniedHandler: CustomOAuth2AccessDeniedHandler,
) {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity, customCorsConfiguration: CustomCorsConfiguration
    ): SecurityFilterChain {
        http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .cors {
                it.configurationSource(
                    customCorsConfiguration.corsConfiguration()
                )
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/login/oauth2/**",
                    "/oauth2/**",
                    "/test/**",
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**",
                    "/onboarding/school"
                ).permitAll()
                .anyRequest().authenticated()
            }
            .exceptionHandling {
                it.accessDeniedHandler(customOAuth2AccessDeniedHandler)
            }
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .oauth2Login{
                it.defaultSuccessUrl("/")
                    .userInfoEndpoint {
                        userinfo -> userinfo.userService(customOAuth2UserService)
                    }
                    .successHandler(customOAuth2SuccessHandler)
            }

        return http.build()
    }
}