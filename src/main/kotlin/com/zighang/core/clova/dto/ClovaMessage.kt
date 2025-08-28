package com.zighang.core.clova.dto

data class ClovaMessage(
    val role: String,

    val content: String
) {
    companion object {
        fun createSystemMessage(content: String): ClovaMessage {
            return ClovaMessage(
                "system",
                content
            )
        }

        fun createUserMessage(content: String): ClovaMessage {
            return ClovaMessage(
                "user",
                content
            )
        }
    }
}