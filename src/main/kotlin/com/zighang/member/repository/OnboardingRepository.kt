package com.zighang.member.repository

import com.zighang.member.entity.Onboarding
import org.springframework.data.jpa.repository.JpaRepository

interface OnboardingRepository : JpaRepository<Onboarding, Long> {

}