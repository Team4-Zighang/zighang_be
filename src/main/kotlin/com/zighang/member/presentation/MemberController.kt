package com.zighang.member.presentation

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.member.entity.Member
import com.zighang.member.repository.MemberRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class UserController(
    private val memberRepository: MemberRepository
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
}