package com.zighang.jobposting.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.jobposting.entity.value.Company
import org.springframework.stereotype.Component

@Component
class CompanyMapper(objectMapper: ObjectMapper) : AbstractObjectMapper<Company>(objectMapper) {

    fun toJsonDto(json: String): Company {
        val fixedJson = json
            .replace("'", "\"")
            .replace("None", "null")
        return parse<Company>(fixedJson, Company::class.java)
    }
}