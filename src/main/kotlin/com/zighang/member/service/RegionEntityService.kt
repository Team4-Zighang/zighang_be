package com.zighang.member.service

import com.zighang.member.entity.RegionEntity
import com.zighang.member.repository.RegionEntityRepository
import org.springframework.stereotype.Service

@Service
class RegionEntityService(
    private val regionEntityRepository: RegionEntityRepository
) {
    fun findAllByOnboardingId(onboardingId : Long) : List<RegionEntity> {
        return regionEntityRepository.findAllByOnboardingId(onboardingId)
    }
}