package com.zighang.member.service

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.member.dto.request.OnboardingRequest
import com.zighang.member.entity.JobRole
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import com.zighang.member.entity.RegionEntity
import com.zighang.member.entity.value.School
import com.zighang.member.repository.JobRoleRepository
import com.zighang.member.repository.OnboardingRepository
import com.zighang.member.repository.RegionEntityRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OnboardingService(
    private val onboardingRepository: OnboardingRepository,
    private val jobRoleRepository: JobRoleRepository,
    private val regionEntityRepository: RegionEntityRepository
) {
    @Transactional(readOnly = true)
    fun findById(id : Long) : Onboarding? {
        return onboardingRepository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    fun getById(id : Long) : Onboarding {
        return findById(id) ?: throw DomainException(GlobalErrorCode.NOT_EXIST_ONBOARDING)
    }

    @Transactional
    fun upsertOnboarding(member : Member, onboardingId: Long?, onboardingRequest: OnboardingRequest) {
        val onboarding = if (onboardingId == null) {
            val newOnboarding = Onboarding.create(
                jobCategory = onboardingRequest.jobCategory,
                careerYear = onboardingRequest.careerYear,
                school = School.fromSchoolName(onboardingRequest.school),
                major = onboardingRequest.major
            )
            val onboardingEntity = onboardingRepository.save(newOnboarding)
            member.completeOnboarding(newOnboarding)
            val newRegions = onboardingRequest.region.map {
                region -> RegionEntity.create(
                    onboardingEntity.id,
                    region
                )
            }
            regionEntityRepository.saveAll(newRegions)
            newOnboarding
        } else {
            val found = onboardingRepository.findById(onboardingId)
                .orElseThrow { NoSuchElementException("Onboarding not found: $onboardingId") }
            found.update(
                jobCategory = onboardingRequest.jobCategory,
                careerYear = onboardingRequest.careerYear,
                school = School.fromSchoolName(onboardingRequest.school),
                major = onboardingRequest.major
            )
            regionEntityRepository.deleteAllByOnboardingId(onboardingId)
            val newRegions = onboardingRequest.region.map {
                    region -> RegionEntity.create(
                onboardingId,
                region
            )
            }
            regionEntityRepository.saveAll(newRegions)
            found
        }

        jobRoleRepository.deleteByOnboardingId(onboarding.id)

        val rolesToSave = onboardingRequest.jobRole
            .map { it.replace(" ", "") }
            .filter { it.isNotEmpty() }
            .distinct()
            .map { role -> JobRole.create(onboardingId = onboarding.id, jobRole = role) }

        if (rolesToSave.isNotEmpty()) {
            jobRoleRepository.saveAll(rolesToSave)
        }

    }
}