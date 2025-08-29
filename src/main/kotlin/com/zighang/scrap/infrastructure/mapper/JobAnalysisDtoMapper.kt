package com.zighang.scrap.infrastructure.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.clova.dto.ChatResponse
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import org.springframework.stereotype.Component

@Component
class JobAnalysisDtoMapper(
    objectMapper: ObjectMapper
)
: AbstractObjectMapper<JobPostingAnalysisDto?>(objectMapper) {
    fun toJsonDto(json: String?): JobPostingAnalysisDto {
        return parse<JobPostingAnalysisDto>(json, JobPostingAnalysisDto::class.java)
    }
}