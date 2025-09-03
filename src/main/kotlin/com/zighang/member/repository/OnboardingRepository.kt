package com.zighang.member.repository

import com.zighang.member.entity.Onboarding
import com.zighang.member.entity.value.School
import org.springframework.data.jpa.repository.JpaRepository

interface OnboardingRepository : JpaRepository<Onboarding, Long> {

    fun findBySchoolAndJobRole(school: School, jobRole: String): List<Onboarding>
}