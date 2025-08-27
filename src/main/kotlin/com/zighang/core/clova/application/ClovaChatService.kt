package com.zighang.core.clova.application

import com.zighang.core.clova.dto.ChatRequest
import com.zighang.core.clova.infrastructure.ClovaChatCompletionCaller
import org.springframework.stereotype.Service

@Service
class ClovaChatService(
    private val clovaChatCompletionCaller: ClovaChatCompletionCaller,
) {

    fun getChat(request: ChatRequest) : String {
        return clovaChatCompletionCaller.clovaChatCompletionApiCaller(
            request.systemMessage, request.userMessage
        );
    }
}