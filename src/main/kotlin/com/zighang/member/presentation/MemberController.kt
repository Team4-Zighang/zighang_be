package com.zighang.member.presentation

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.member.dto.request.OnboardingRequest
import com.zighang.member.entity.Member
import com.zighang.member.facade.MemberFacade
import com.zighang.member.repository.MemberRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
@Tag(name = "유저 컨트롤러", description = "UserController")
class MemberController(
    private val memberRepository: MemberRepository,
    private val memberFacade:MemberFacade
) {
    @GetMapping("/me")
    fun getMyInfo(@AuthenticationPrincipal member: CustomUserDetails): ResponseEntity<RestResponse<Member>> {
        println(member.getId())
        return ResponseEntity.ok(
            RestResponse(
                memberRepository.findById(member.getId())
                    .orElseThrow { IllegalArgumentException("유저 정보를 찾을 수 없습니다.") }
            )
        )
    }

    @PostMapping("/onboarding")
    @Operation(summary = "온보딩 하기", description = "createMember")
    fun createMember(@AuthenticationPrincipal member: CustomUserDetails,
                     @RequestBody onboardingRequest: OnboardingRequest) : ResponseEntity<RestResponse<Boolean>> {
        memberFacade.onboarding(member, onboardingRequest)
        return ResponseEntity.ok(RestResponse(true))
    }
}