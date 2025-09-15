package com.zighang.jobposting.infrastructure.mapper

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.jobposting.entity.value.Company
import org.springframework.stereotype.Component

@Component
class CompanyMapper(objectMapper: ObjectMapper) : AbstractObjectMapper<Company>(objectMapper) {

    fun toJsonDto(json: String): Company {
        println(json)
        val mapper = objectMapper.copy().apply {
            configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        }

        // None -> null 치환
        var fixedJson = json.replace(Regex("""(?<=[:\s\[,])None(?=[,\]\s}])"""), "null")

        // 잘못된 \x??, \m 같은 escape 제거
        fixedJson = fixedJson.replace(Regex("""\\[^"\\/]"""), "")

        return mapper.readValue(fixedJson, Company::class.java)
    }
}