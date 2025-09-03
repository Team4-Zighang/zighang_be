package com.zighang.card.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import org.springframework.stereotype.Component

@Component
class CardJobPosingAnalysisDtoMapper(
    objectMapper: ObjectMapper
) : AbstractObjectMapper<CardJobPostingAnalysisDto?>(objectMapper) {
    fun toJsonDto(json: String?): CardJobPostingAnalysisDto {
        return parse(json, CardJobPostingAnalysisDto::class.java)
    }
}