package com.zighang.jobposting.infrastructure.mapper

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.jobposting.entity.value.Company
import org.springframework.stereotype.Component

@Component
class CompanyMapper(objectMapper: ObjectMapper) : AbstractObjectMapper<Company>(objectMapper) {

    fun toJsonDto(json: String): Company {
        val fixedJson = json.replace(Regex("""(?<=[:\s\[,])None(?=[,\]\s}])"""), "null")
            .replace("'", "\"")

        val mapper = objectMapper.copy().apply {
            configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        }

        return mapper.readValue(fixedJson, Company::class.java)
    }
}