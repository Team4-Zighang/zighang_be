package com.zighang.core.infrastructure

import com.zighang.member.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val member: Member,
) : UserDetails {
    fun getId(): Long {
        return member.id
    }
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${member.role.name}"))
    }

    override fun getPassword(): String {
        return "";
    }

    override fun getUsername(): String {
        return member.name
    }
}