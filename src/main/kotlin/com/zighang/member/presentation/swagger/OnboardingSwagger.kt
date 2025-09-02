package com.zighang.member.presentation.swagger

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.core.presentation.RestResponse
import com.zighang.member.dto.request.OnboardingRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Onboarding", description = "온보딩 관련 컨트롤러")
interface OnboardingSwagger {

    @Operation(
        summary = "학교 이름 불러오는 컨트롤러",
        description = "드롭다운에 쓰일 학교이름을 불러옵니다.",
        operationId = "/onboarding/school"
    )
    fun getSchool(): ResponseEntity<RestResponse<List<String>>>

    @Operation(
        summary = "온보딩 진행",
        description = "온보딩을 진행합니다.",
        operationId = "/onboarding"
    )
    fun createMember(@AuthenticationPrincipal member: CustomUserDetails,
                     @RequestBody onboardingRequest: OnboardingRequest
    ) : ResponseEntity<RestResponse<Boolean>>
}