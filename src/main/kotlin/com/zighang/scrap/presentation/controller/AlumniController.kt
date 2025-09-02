package com.zighang.scrap.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.PageResponse
import com.zighang.core.presentation.RestResponse
import com.zighang.scrap.dto.response.AlumniSimiliarJobPostingResponseDto
import com.zighang.scrap.presentation.swagger.AlumniSwagger
import com.zighang.scrap.service.AlumniService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/alumni")
class AlumniController(
    private val alumniService: AlumniService
) : AlumniSwagger {

    @GetMapping("/scrap/list")
    override fun getScrappedJobPostingsBySimilarUsersController(
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestParam page: Int
    ) : ResponseEntity<RestResponse<PageResponse<AlumniSimiliarJobPostingResponseDto>>> {
        return ResponseEntity.ok(
            RestResponse<PageResponse<AlumniSimiliarJobPostingResponseDto>>(
                PageResponse.from(
                    alumniService.getScrappedJobPostingsBySimilarUsers(
                        customUserDetails, page
                    )
                )
            )
        )
    }

}