package com.zighang.member.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.member.dto.request.OnboardingRequest
import com.zighang.member.entity.value.School
import com.zighang.member.facade.MemberFacade
import com.zighang.member.presentation.swagger.OnboardingSwagger
import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/onboarding")
class OnboardingController(
    private val memberFacade: MemberFacade
) : OnboardingSwagger {

    @GetMapping("/school")
    override fun getSchool(): ResponseEntity<RestResponse<List<String>>> {
        return ResponseEntity.ok(
            RestResponse<List<String>>(School.allSchoolNames)
        );
    }

    @PostMapping
    @Operation(summary = "온보딩 하기", description = "createMember")
    override fun createMember(@AuthenticationPrincipal member: CustomUserDetails,
                     @RequestBody onboardingRequest: OnboardingRequest
    ) : ResponseEntity<RestResponse<Boolean>> {
        memberFacade.onboarding(member, onboardingRequest)
        return ResponseEntity.ok(RestResponse(true))
    }
}