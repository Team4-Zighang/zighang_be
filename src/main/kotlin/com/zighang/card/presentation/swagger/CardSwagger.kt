package com.zighang.card.presentation.swagger

import com.zighang.card.dto.CardContentResponse
import com.zighang.card.dto.GetCardPositionRequest
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody

interface CardSwagger {
    @Operation(
        summary = "카드 소환하기",
        description = "카드를 소환하는 api. 카드를 재소환하거나 카드를 새로 소환해야할때(아직 카드를 소환한 적이 없을때만) 불러라",
        operationId = "",
    )
    fun createCardSet(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
    ) : ResponseEntity<RestResponse<Boolean>>

    @Operation(
        summary = "카드 오픈",
        description = "카드를 오픈한다.",
        operationId = "/show"
    )
    fun openCard(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody cardPosition: GetCardPositionRequest
    ) : ResponseEntity<RestResponse<CardContentResponse>>

    @Operation(
        summary = "카드 한 장 재오픈",
        description = "카드 한 장을 재오픈한다., 카드 한 장의 시간이 초과되면 프론트측에서는 이 api를 부르면 됩니다.",
        operationId = "/replace"
    )
    fun replace(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody cardPosition: GetCardPositionRequest
    ) : ResponseEntity<RestResponse<Boolean>>

    @Operation(
        summary = "열린 카드 조회",
        description = "열린 카드를 조회한다.",
        operationId = "/show/open"
    )
    fun showOpenCard(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ) : ResponseEntity<RestResponse<List<CardContentResponse>>>
}