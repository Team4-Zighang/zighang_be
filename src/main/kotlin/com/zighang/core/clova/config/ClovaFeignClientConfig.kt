package com.zighang.core.clova.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class ClovaFeignClientConfig(

    @Value("\${ncp.clova.api-key}")
    private val clovaApiKey: String,
) {

    @Bean
    fun clovaRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor { restTemplate: RequestTemplate ->
            restTemplate.header("Authorization", "Bearer $clovaApiKey")
            restTemplate.header("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString())
            restTemplate.header("Content-Type", "application/json")
        }
    }
}