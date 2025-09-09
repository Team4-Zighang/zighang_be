package com.zighang.member.repository

import com.zighang.member.entity.Onboarding
import com.zighang.member.entity.value.School
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OnboardingRepository : JpaRepository<Onboarding, Long> {

    @Query("""
        SELECT DISTINCT o 
        FROM Onboarding o
        INNER JOIN JobRole jr ON jr.onboardingId = o.id
        WHERE o.school = :school
        AND jr.jobRole IN :jobRoles
    """
    )
    fun findBySchoolAndJobRoleIn(school: School, jobRoles: List<String>): List<Onboarding>

    fun findByIdIn(idList: List<Long>): List<Onboarding>
}