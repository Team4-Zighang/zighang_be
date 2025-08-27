package com.zighang.core.clova.application

import com.zighang.core.clova.dto.ChatRequest
import com.zighang.core.clova.dto.ChatResponse
import com.zighang.core.clova.infrastructure.ClovaChatCompletionCaller
import com.zighang.core.clova.infrastructure.ClovaResponseMapper
import org.springframework.stereotype.Service

@Service
class ClovaChatService(
    private val clovaChatCompletionCaller: ClovaChatCompletionCaller,
    private val clovaResponseMapper: ClovaResponseMapper,
) {

    fun getChat(request: ChatRequest) : ChatResponse? {
        return clovaResponseMapper.toJobDescriptionDto(
            clovaChatCompletionCaller.clovaChatCompletionApiCaller(
                request.systemMessage, request.userMessage
            )
        );
    }
}