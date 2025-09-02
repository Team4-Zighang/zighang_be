package com.zighang.member.presentation.controller

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.member.entity.Member
import com.zighang.member.presentation.swagger.MemberSwagger
import com.zighang.member.repository.MemberRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController(
    private val memberRepository: MemberRepository
) : MemberSwagger {

    @GetMapping("/me")
    override fun getMyInfo(@AuthenticationPrincipal customUserDetails: CustomUserDetails): ResponseEntity<RestResponse<Member>> {
        return ResponseEntity.ok(
            RestResponse(
                memberRepository.findById(customUserDetails.getId())
                    .orElseThrow { IllegalArgumentException("유저 정보를 찾을 수 없습니다.") }
            )
        )
    }
}