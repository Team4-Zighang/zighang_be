package com.zighang.card.presentation

import com.zighang.card.dto.CardContentResponse
import com.zighang.card.dto.GetCardPositionRequest
import com.zighang.card.dto.RemainScrapResponse
import com.zighang.card.facade.CardFacade
import com.zighang.card.presentation.swagger.CardSwagger
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/card")
@Tag(name = "Card", description = "카드 관련 API")
class CardController(
    private val cardFacade: CardFacade
) : CardSwagger {

    @PostMapping("")
    override fun createCardSet(@AuthenticationPrincipal customUserDetails: CustomUserDetails):
            ResponseEntity<RestResponse<Boolean>> {
        cardFacade.createCard(customUserDetails)
        return ResponseEntity.ok(
            RestResponse(
                true
            )
        )
    }

    @PostMapping("/show")
    override fun openCard(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody cardPosition: GetCardPositionRequest) :
            ResponseEntity<RestResponse<CardContentResponse>> {
        return ResponseEntity.ok(
            RestResponse(
                cardFacade.getCard(customUserDetails, cardPosition.position)
            )
        )
    }

    @PostMapping("/replace")
    override fun replace(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody cardPosition: GetCardPositionRequest
    ): ResponseEntity<RestResponse<Boolean>> {
        return ResponseEntity.ok(
            RestResponse(
                cardFacade.replace(customUserDetails, cardPosition.position)
            )
        )
    }

    @GetMapping("/show/open")
    override fun showOpenCard(@AuthenticationPrincipal customUserDetails: CustomUserDetails): ResponseEntity<RestResponse<List<CardContentResponse>>> {
        return ResponseEntity.ok(
            RestResponse(
                cardFacade.showOpenList(customUserDetails)
            )
        )
    }

    @GetMapping("/remain-scrap")
    override fun showScrap(customUserDetails: CustomUserDetails): ResponseEntity<RestResponse<RemainScrapResponse>> {
        return ResponseEntity.ok(
            RestResponse(
                cardFacade.showScrap(customUserDetails)
            )
        )
    }

}