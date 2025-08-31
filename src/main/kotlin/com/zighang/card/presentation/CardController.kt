package com.zighang.card.presentation

import com.zighang.card.dto.CreateCardSetResponse
import com.zighang.card.facade.CardFacade
import com.zighang.card.presentation.swagger.CardSwagger
import com.zighang.card.service.CardService
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.memo.dto.response.MemoCreateResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/card")
@Tag(name = "Card", description = "카드 관련 API")
class CardController(
    private val cardFacade: CardFacade
) : CardSwagger {

    @PostMapping("")
    override fun createCardSet(@AuthenticationPrincipal customUserDetails: CustomUserDetails):
            ResponseEntity<RestResponse<CreateCardSetResponse>> {
        return ResponseEntity.ok(
            RestResponse<CreateCardSetResponse>(
                cardFacade.createCard(customUserDetails)
            )
        )
    }



}