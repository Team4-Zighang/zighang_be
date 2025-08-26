package com.zighang.memo.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.memo.dto.request.MemoCreateRequest
import com.zighang.memo.dto.response.MemoCreateResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

interface MemoSwagger {

    @Operation(
        summary = "메모 저장하기",
        description = "메모를 저장합니다.<br>로그인 상태에서만 동작해야 합니다.<br>메모가 이미 존재하는 경우 자동으로 업데이트 됩니다.",
        operationId = "/memo/save",
    )
    fun saveMemo(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody memoCreateRequest: MemoCreateRequest,
    ) : ResponseEntity<RestResponse<MemoCreateResponse>>

    @Operation(
        summary = "해당 공고에 대한 메모 보기",
        description = "해당 공고에 저장한 메모를 봅니다.<br>공고에 메모를 작성하지 않은 경우 null이 리턴됩니다.",
        operationId = "/memo/{postingId}",
    )
    fun getMemo(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable postingId: Long,
    ): ResponseEntity<RestResponse<String?>>
}