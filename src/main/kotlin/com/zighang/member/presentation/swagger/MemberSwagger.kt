package com.zighang.member.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.member.entity.Member
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal

@Tag(name = "Member", description = "멤버 정보 관련 컨트롤러")
interface MemberSwagger {

    @Operation(
        summary = "유저 정보 불러오기 컨트롤러",
        description = "로그인 이후 유저 정보를 불러오는 것이 필요한 경우 사용하는 컨트롤러",
        operationId = "/member/me"
    )
    fun getMyInfo(@AuthenticationPrincipal customUserDetails: CustomUserDetails) : ResponseEntity<RestResponse<Member>>
}