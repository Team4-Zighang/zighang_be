package com.zighang.scrap.infrastructure

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
            SystemMessageFactory.jobAnalysisSystemMessageFactory(),
            ocrData
        )

        return clovaResponseMapper.toJsonDto(result)
    }

    fun getCardJobResponse(ocrData: String) : ChatResponse {
        val systemMessage = SystemMessageFactory.cardJobInfoMessageFactory()

        require(systemMessage.isNotBlank()) { "jobAnalysis system message must not be blank" }
        require(ocrData.isNotBlank()) { "ocrData must not be blank" }

        val result = clovaChatCompletionCaller.clovaChatCompletionApiCaller(
            SystemMessageFactory.jobAnalysisSystemMessageFactory(),
            ocrData
        )

        return clovaResponseMapper.toJsonDto(result)
    }
}