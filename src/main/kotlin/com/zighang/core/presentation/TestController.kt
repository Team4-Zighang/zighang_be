package com.zighang.core.presentation

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    
    @GetMapping("/hello")
    fun getHello(): ResponseEntity<RestResponse<String>> {
        return ResponseEntity.ok(RestResponse("Hello"))
    }
}