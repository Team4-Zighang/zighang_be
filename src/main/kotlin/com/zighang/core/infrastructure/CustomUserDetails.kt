package com.zighang.core.infrastructure

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    // Todo 멤버 테이블 확정되면 주석 해제
//    private val member: Member,
) : UserDetails {
    fun getId(): Long {
//        return member.id
        return 1L;
    }
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_ADMIN"))
//        return mutableListOf(SimpleGrantedAuthority("ROLE_${member.Role.name}"))
    }

    override fun getPassword(): String {
        TODO("Not yet implemented")
    }

    override fun getUsername(): String {
        TODO("Not yet implemented")
    }
}