package com.zighang.member.repository

import com.zighang.member.entity.JobRole
import org.springframework.data.jpa.repository.JpaRepository

interface JobRoleRepository : JpaRepository<JobRole, Long> {
    fun deleteByOnboardingId(onboardingId : Long)
    fun findByOnboardingId(onboardingId: Long) : List<JobRole>
}