package com.zighang.scrap.infrastructure.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.scrap.dto.request.PersonalityValueDto
import org.springframework.stereotype.Component

@Component
class PersonalityValueDtoMapper(objectMapper: ObjectMapper)
    : AbstractObjectMapper<PersonalityValueDto>(objectMapper) {
        fun toDto(json: String): PersonalityValueDto {
            return parse(json, PersonalityValueDto::class.java)
        }
}