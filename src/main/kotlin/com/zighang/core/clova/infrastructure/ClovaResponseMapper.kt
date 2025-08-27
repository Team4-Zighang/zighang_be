package com.zighang.core.clova.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.clova.dto.ChatResponse
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import org.springframework.stereotype.Component


@Component
class ClovaResponseMapper(objectMapper: ObjectMapper?) :
    AbstractObjectMapper<ChatResponse?>(objectMapper!!) {
    fun toJobDescriptionDto(json: String?): ChatResponse? {
        return parse<ChatResponse>(json, ChatResponse::class.java)
    }
}