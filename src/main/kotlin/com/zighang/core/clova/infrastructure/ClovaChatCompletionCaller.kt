package com.zighang.core.clova.infrastructure

import com.zighang.core.clova.dto.ClovaMessage
import com.zighang.core.clova.dto.ClovaStudioRequest
import com.zighang.core.exception.GlobalErrorCode
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Component
import java.util.List


@Component
class ClovaChatCompletionCaller(
    private val clovaFeignClient: ClovaFeignClient
) {
    fun clovaChatCompletionApiCaller(
        systemMessage: String?, userMessage: String?
    ): String {
        val clovaStudioRequest: ClovaStudioRequest = ClovaStudioRequest(
            listOf(
                ClovaMessage.createSystemMessage(systemMessage!!),
                ClovaMessage.createUserMessage(userMessage!!)
            ),
            MAX_TOKEN
        )

        try {
            return clovaFeignClient.callClovaStudio(
                clovaStudioRequest
            )
        } catch (e: Exception) {
            throw GlobalErrorCode.CLOVA_API_CALL_FAILED.toException()
        }
    }

    companion object {
        private const val MAX_TOKEN = 512
    }
}