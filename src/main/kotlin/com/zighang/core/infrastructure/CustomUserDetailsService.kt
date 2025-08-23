package com.zighang.core.infrastructure

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.core.infrastructure.CustomUserDetails
import com.zighang.member.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberRepository: MemberRepository,
) {
    fun loadUserById(id: Long): UserDetails =
        memberRepository.findById(id)
            .map { CustomUserDetails(it) }
            .orElseThrow { DomainException(GlobalErrorCode.NOT_EXIST_MEMBER) }
}