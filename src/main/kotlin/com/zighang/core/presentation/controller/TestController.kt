package com.zighang.core.presentation.controller

import com.zighang.core.config.swagger.ApiErrorCode
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.presentation.RestResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/test")
class TestController {
    
    @GetMapping("/hello")
    fun getHello(): ResponseEntity<RestResponse<String>> {
        return ResponseEntity.ok(RestResponse("Hello"))
    }

    @GetMapping("/error")
    @ApiErrorCode(value = [GlobalErrorCode::class])
    fun getError(): ResponseEntity<RestResponse<String>> {
        throw DomainException("error")
    }
}