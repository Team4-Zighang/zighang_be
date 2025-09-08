package com.zighang.member.facade

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.member.dto.request.OnboardingRequest
import com.zighang.member.service.MemberService
import com.zighang.member.service.OnboardingService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberFacade(
    private val memberService : MemberService,
    private val onboardingService: OnboardingService
) {
    @Transactional
    fun onboarding(customUserDetails: CustomUserDetails, onboardingRequest: OnboardingRequest) {
        val member = memberService.getById(customUserDetails.getId())
        onboardingService.upsertOnboarding(member.onboardingId, onboardingRequest)
    }
}