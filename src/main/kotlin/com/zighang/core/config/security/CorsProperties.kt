package com.zighang.core.config.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "cors")
data class CorsProperties (
    var allowedOrigins: List<String> = emptyList(),
    var allowedMethods: List<String> = emptyList(),
    var allowedHeaders: List<String> = emptyList(),
    var allowCredentials: Boolean = false,
    var exposedHeaders: List<String> = emptyList(),
    var maxAge: Long = 3600L
)