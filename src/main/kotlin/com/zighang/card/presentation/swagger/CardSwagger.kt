package com.zighang.card.presentation.swagger

import com.zighang.card.dto.CreateCardSetResponse
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.memo.dto.response.MemoCreateResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable

interface CardSwagger {
    @Operation(
        summary = "카드 소환하기",
        description = "카드를 소환하는 api. 카드를 재소환하거나 카드를 새로 소환해야할때(아직 카드를 소환한 적이 없을때만) 불러라",
        operationId = "",
    )
    fun createCardSet(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
    ) : ResponseEntity<RestResponse<CreateCardSetResponse>>

    @Operation(
        summary = "카드 오픈",
        description = "카드를 오픈한다.",
        operationId = "/{cardId}"
    )
    fun openCard(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @PathVariable(name = "cardId") cardId : Long
    )
}