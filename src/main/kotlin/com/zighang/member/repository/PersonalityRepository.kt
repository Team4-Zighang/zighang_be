package com.zighang.member.repository

import com.zighang.member.entity.Personality
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonalityRepository : CrudRepository<Personality, Long> {
    fun findByMemberId(memberId: Long): Personality?
}