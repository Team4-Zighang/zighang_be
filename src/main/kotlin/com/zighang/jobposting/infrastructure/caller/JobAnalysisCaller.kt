package com.zighang.jobposting.infrastructure.caller

import com.zighang.core.clova.dto.ChatResponse
import com.zighang.core.clova.infrastructure.ClovaChatCompletionCaller
import com.zighang.core.clova.infrastructure.ClovaResponseMapper
import com.zighang.scrap.util.SystemMessageFactory
import org.springframework.stereotype.Component

@Component
class JobAnalysisCaller(
    private val clovaChatCompletionCaller: ClovaChatCompletionCaller,
    private val clovaResponseMapper: ClovaResponseMapper
) {

    fun call(ocrData : String) : ChatResponse {
        val systemMessage = SystemMessageFactory.jobAnalysisSystemMessageFactory()

        require(systemMessage.isNotBlank()) { "jobAnalysis system message must not be blank" }
        require(ocrData.isNotBlank()) { "ocrData must not be blank" }

        val result = clovaChatCompletionCaller.clovaChatCompletionApiCaller(
            systemMessage,
            ocrData
        )

        return clovaResponseMapper.toJsonDto(result)
    }

    fun extractCarrier(ocrSummary : String) : ChatResponse {
        val systemMessage = SystemMessageFactory.analysisCareerMessageFactory()
        require(systemMessage.isNotBlank()) { "careerAnalysis system message must not be blank" }
        require(ocrSummary.isNotBlank()) { "ocrData must not be blank" }

        val result = clovaChatCompletionCaller.clovaChatCompletionApiCaller(
            systemMessage,
            ocrSummary
        )

        return clovaResponseMapper.toJsonDto(result)
    }
}