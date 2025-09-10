package com.zighang.member.service

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.member.dto.MemberDto
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import com.zighang.member.repository.JobRoleRepository
import com.zighang.member.repository.MemberRepository
import com.zighang.member.repository.OnboardingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val onboardingRepository: OnboardingRepository,
    private val jobRoleRepository: JobRoleRepository
) {
    @Transactional(readOnly = true)
    fun findById(id : Long) : Member? {
        return memberRepository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    fun getById(id : Long) : Member {
        return findById(id) ?: throw DomainException(GlobalErrorCode.NOT_EXIST_MEMBER)
    }

    @Transactional
    fun completeOnboarding(member: Member, onboarding: Onboarding) {
        member.completeOnboarding(onboarding)
    }

    @Transactional(readOnly = true)
    fun getMemberInfo(customUserDetails: CustomUserDetails) : MemberDto {
        val member = getById(customUserDetails.getId())

        val onboardingId = member.onboardingId ?: return MemberDto(member, null, null)

        val onboarding = onboardingRepository.findById(onboardingId).orElse(null)

        val jobRole = if(onboarding == null) emptyList() else jobRoleRepository.findByOnboardingId(onboardingId)

        return MemberDto(
            member,
            onboarding,
            jobRole
        )
    }
}