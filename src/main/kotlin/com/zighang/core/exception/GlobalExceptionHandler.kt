package com.zighang.core.exception

import com.zighang.core.presentation.ErrorResponse
import org.slf4j.LoggerFactory
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
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.createValidationErrorResponse(400, e))
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomainExceptionHandler(e: DomainException): ResponseEntity<ErrorResponse> {
        log.warn("Domain Exception: {}", e.message)
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.createDomainErrorResponse(500, e))
    }

    @ExceptionHandler(Exception::class)
    fun handleExceptionHandler(e: Exception): ResponseEntity<ErrorResponse> {
        log.warn("Exception: {}", e.message)
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.createErrorResponse(500, e))
    }
}