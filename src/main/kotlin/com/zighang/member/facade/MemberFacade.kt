package com.zighang.member.facade

import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.member.dto.request.MajorRequest
import com.zighang.member.dto.request.OnboardingRequest
import com.zighang.member.entity.value.School
import com.zighang.member.service.MajorService
import com.zighang.member.service.MemberService
import com.zighang.member.service.OnboardingService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberFacade(
    private val memberService : MemberService,
    private val onboardingService: OnboardingService,
    private val majorService: MajorService
) {
    @Transactional
    fun onboarding(customUserDetails: CustomUserDetails, onboardingRequest: OnboardingRequest) {
        val member = memberService.getById(customUserDetails.getId())
        onboardingService.upsertOnboarding(member, member.onboardingId, onboardingRequest)
    }

    fun getMajor(school : School): List<String> {
        val majorList = majorService.getBySchool(school)
        return majorList.map {
            major -> major.majorName
        }
    }
}