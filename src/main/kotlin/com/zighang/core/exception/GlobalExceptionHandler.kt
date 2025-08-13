package com.zighang.core.exception

import com.zighang.core.presentation.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object{
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        log.warn("Validation Failed: {}", e.message)
        val statusCode = e.statusCode.value()

        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.createValidationErrorResponse(statusCode, e))
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainExceptionHandler(e: DomainException): ResponseEntity<ErrorResponse> {
        log.warn("Domain Exception: {}", e.message)
        val statusCode = e.httpStatus?.value() ?: HttpStatus.INTERNAL_SERVER_ERROR.value()

        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.createDomainErrorResponse(statusCode, e))
    }

    @ExceptionHandler(Exception::class)
    fun handleExceptionHandler(e: Exception): ResponseEntity<ErrorResponse> {
        log.warn("Exception: {}", e.message)
        val statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()

        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.createErrorResponse(statusCode, e))
    }
}