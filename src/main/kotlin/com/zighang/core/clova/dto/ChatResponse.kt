package com.zighang.core.clova.dto

data class ChatResponse(
    val result: Result
) {
    data class Result(
        val message: Message,
        val finishReason: String,
        val usage: Usage
    )

    data class Message(
        val role: String,
        val content: String
    )

    data class Usage(
        val promptTokens: Int,
        val completionTokens: Int,
        val totalTokens: Int
    )
}