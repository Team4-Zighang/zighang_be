package com.zighang.member.repository

import com.zighang.member.entity.Member
import org.springframework.data.repository.CrudRepository

interface MemberRepository : CrudRepository<Member, Long> {

    fun findByEmail(email: String): Member?

    fun findByOnboardingIdIn(id: List<Long>) : List<Member>
}