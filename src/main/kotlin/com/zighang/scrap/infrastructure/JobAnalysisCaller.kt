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
        val result = clovaChatCompletionCaller.clovaChatCompletionApiCaller(
            SystemMessageFactory.jobAnalysisSystemMessageFactory(),
            ocrData
        )

        return clovaResponseMapper.toJsonDto(result)
    }
}