package com.zighang.member.service

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import com.zighang.member.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository
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
}