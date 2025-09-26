package com.zighang.core.presentation.controller

import com.zighang.core.application.ObjectStorageService
import com.zighang.core.clova.application.ClovaChatService
import com.zighang.core.clova.dto.ChatRequest
import com.zighang.core.clova.dto.ChatResponse
import com.zighang.core.config.rabbitmq.TestEventPublisher
import com.zighang.core.config.swagger.ApiErrorCode
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.jwt.TokenService
import com.zighang.core.presentation.RestResponse
import com.zighang.member.repository.MemberRepository
import io.swagger.v3.oas.annotations.Operation
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController()
@RequestMapping("/test")
class TestController(
    private val objectStorageService: ObjectStorageService,
    private val clovaChatService: ClovaChatService,
    private val testEventPublisher: TestEventPublisher,
    private val tokenService: TokenService,
    private val memberRepository: MemberRepository
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

    @GetMapping("/token")
    @Operation(
        summary = "테스트용 토큰 발급",
        description = "테스트 용으로 토큰을 발급합니다. 해당 API를 실행한뒤 /member/me를 통해 유저 정보를 불러옵니다.",
        operationId = "/test/token"
    )
    @Profile("local")
    fun token(
        @RequestParam memberId: Long
    ): ResponseEntity<RestResponse<String>> {
        val member = memberRepository.findById(memberId)

        return ResponseEntity.ok(
            RestResponse(
                tokenService.provideAccessToken(memberId, member.get().role.name)
            )
        )
    }
}