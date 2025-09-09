package com.zighang.jobposting.infrastructure.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.jobposting.dto.CareerAnalysisDto
import org.springframework.stereotype.Component

@Component
class CareerAnalysisMapper(objectMapper: ObjectMapper) : AbstractObjectMapper<CareerAnalysisDto>(objectMapper) {
    fun toJsonDto(json: String?): CareerAnalysisDto {
        require(!json.isNullOrBlank()) { "CareereAnalysis json must not be null or blank" }

        return parse<CareerAnalysisDto>(json, CareerAnalysisDto::class.java)
    }
}