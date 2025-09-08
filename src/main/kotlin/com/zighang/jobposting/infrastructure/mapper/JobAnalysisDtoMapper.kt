package com.zighang.jobposting.infrastructure.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.infrastructure.mapper.AbstractObjectMapper
import com.zighang.scrap.dto.response.JobPostingAnalysisDto
import org.springframework.stereotype.Component

@Component
class JobAnalysisDtoMapper(
    objectMapper: ObjectMapper
)
: AbstractObjectMapper<JobPostingAnalysisDto?>(objectMapper) {
    fun toJsonDto(json: String?): JobPostingAnalysisDto {
        require(!json.isNullOrBlank()) { "JobPostingAnalysis json must not be null or blank" }

        return parse<JobPostingAnalysisDto>(json, JobPostingAnalysisDto::class.java)
    }
}