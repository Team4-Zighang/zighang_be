package com.zighang.member.repository

import com.zighang.member.entity.RegionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RegionEntityRepository : JpaRepository<RegionEntity, Long> {
    fun deleteAllByOnboardingId(onboardingId : Long)
    fun findAllByOnboardingId(onboardingId: Long) : List<RegionEntity>
}