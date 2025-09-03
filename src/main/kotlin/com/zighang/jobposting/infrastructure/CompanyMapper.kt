package com.zighang.jobposting.infrastructure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.jobposting.entity.value.Company
import org.springframework.stereotype.Component

@Component
class CompanyMapper(objectMapper: ObjectMapper) : AbstractObjectMapper<Company>(objectMapper) {

    init {
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
    }

    fun toJsonDto(json: String): Company {
        val fixedJson = json.replace(Regex("""(?<=[:\s\[,])None(?=[,\]\s}])"""), "null")
        return parse(fixedJson, Company::class.java)
    }
}