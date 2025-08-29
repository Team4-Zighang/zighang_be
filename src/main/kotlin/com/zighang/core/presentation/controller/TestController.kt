package com.zighang.core.presentation.controller

import com.zighang.core.application.ObjectStorageService
import com.zighang.core.clova.application.ClovaChatService
import com.zighang.core.clova.dto.ChatRequest
import com.zighang.core.clova.dto.ChatResponse
import com.zighang.core.config.rabbitmq.TestEventPublisher
import com.zighang.core.config.swagger.ApiErrorCode
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.presentation.RestResponse
import org.apache.http.entity.ContentType.MULTIPART_FORM_DATA
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController()
@RequestMapping("/test")
class TestController(
    private val objectStorageService: ObjectStorageService,
    private val clovaChatService: ClovaChatService,
    private val testEventPublisher: TestEventPublisher
) {
    
    @GetMapping("/hello")
    fun getHello(): ResponseEntity<RestResponse<String>> {
        return ResponseEntity.ok(RestResponse("Hello"))
    }

    @GetMapping("/error")
    @ApiErrorCode(value = [GlobalErrorCode::class])
    fun getError(): ResponseEntity<RestResponse<String>> {
        throw DomainException("error")
    }

    @PostMapping(
        value = ["/file"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadFile(
        @RequestPart file: MultipartFile,
        @RequestParam id: Long
    ) : ResponseEntity<RestResponse<String>> {
        return ResponseEntity.ok(
            RestResponse(objectStorageService.uploadResumeFile(file, id))
        )
    }

    @DeleteMapping("/file")
    fun uploadFile(
        @RequestParam url: String
    ) : ResponseEntity<RestResponse<Unit>> {
        return ResponseEntity.ok(
            RestResponse(objectStorageService.deleteFile(url))
        )
    }

    @PostMapping("/clova")
    fun chatWithClova(
        @RequestBody chatRequest: ChatRequest
    ) : ResponseEntity<RestResponse<ChatResponse?>> {
        return ResponseEntity.ok(
            RestResponse(clovaChatService.getChat(chatRequest))
        )
    }

    @PostMapping("/rabbit")
    fun chatWithrabbit(
        @RequestBody chatRequest: ChatRequest
    ) {
        testEventPublisher.testPublisher(chatRequest)
    }
}