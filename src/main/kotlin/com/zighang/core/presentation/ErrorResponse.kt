package com.zighang.core.presentation

import com.zighang.core.exception.DomainException
import org.springframework.web.bind.MethodArgumentNotValidException
import java.time.LocalDateTime

class ErrorResponse(
    val statusCode: Int,
    val code: String,
    val message: String
): BaseResponse(
    success = false,
    timestamp = LocalDateTime.now()
) {
    companion object {
        fun createErrorResponse(statusCode: Int, exception: Exception): ErrorResponse {
            return ErrorResponse(
                statusCode = statusCode,
                code = exception::class.simpleName ?: "UnknownException",
                message = exception.message ?: "UnknownException"
            )
        }

        fun createValidationErrorResponse(statusCode: Int, exception: MethodArgumentNotValidException): ErrorResponse {
            return ErrorResponse(
                statusCode = statusCode,
                code = exception::class.simpleName ?: "UnknownException",
                message = exception.message
            )
        }

        fun createSwaggerResponse(statusCode: Int, exception: Exception): ErrorResponse {
            return ErrorResponse(
                statusCode = statusCode,
                code = exception::class.simpleName ?: "UnknownException",
                message = exception.message ?: "No message available"
            )
        }

        fun createDomainErrorResponse(statusCode: Int, exception: DomainException): ErrorResponse {
            return ErrorResponse(
                statusCode = statusCode,
                code = exception::class.simpleName ?: "UnknownException",
                message = exception.message
            )
        }
    }
}