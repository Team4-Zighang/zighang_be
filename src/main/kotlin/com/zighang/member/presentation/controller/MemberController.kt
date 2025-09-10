package com.zighang.member.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.member.dto.MemberDto
import com.zighang.member.entity.Member
import com.zighang.member.presentation.swagger.MemberSwagger
import com.zighang.member.repository.MemberRepository
import com.zighang.member.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController(
    private val memberService: MemberService
) : MemberSwagger {

    @GetMapping("/me")
    override fun getMyInfo(@AuthenticationPrincipal customUserDetails: CustomUserDetails)
    : ResponseEntity<RestResponse<MemberDto>> {
        return ResponseEntity.ok(
            RestResponse(
                memberService.getMemberInfo(customUserDetails)
            )
        )
    }
}