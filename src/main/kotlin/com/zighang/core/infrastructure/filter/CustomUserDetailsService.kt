package com.zighang.core.infrastructure.filter

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    //Todo 멤버 테이블 확정되면 주석해제
//    private val memberRepository: MemberRepository,
) {
    fun loadUserById(id: Long): UserDetails =
//        memberRepository.findById(id)
//            .map { CustomUserDetails(it) }
//            .orElseThrow { DomainException(GlobalErrorCode.NOT_EXIST_MEMBER) }
        CustomUserDetails()
}