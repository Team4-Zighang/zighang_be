package com.zighang.jobposting.infrastructure.mapper

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.jobposting.entity.value.Content
import org.springframework.boot.convert.ApplicationConversionService.configure
import org.springframework.stereotype.Component

@Component
class ContentMapper (
    objectMapper: ObjectMapper
) : AbstractObjectMapper<Content>(objectMapper) {
    fun toJsonDto(json: String): Content {
        val mapper = objectMapper.copy().apply {
            configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        }

        // None -> null 처리
        val fixedJson = json.replace(Regex("""(?<=[:\s\[,])None(?=[,\]\s}])"""), "null")

        return mapper.readValue(fixedJson, Content::class.java)
    }
}