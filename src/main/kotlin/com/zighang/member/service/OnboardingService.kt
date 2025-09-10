package com.zighang.member.service

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.member.dto.request.OnboardingRequest
import com.zighang.member.entity.JobRole
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import com.zighang.member.entity.value.School
import com.zighang.member.repository.JobRoleRepository
import com.zighang.member.repository.OnboardingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OnboardingService(
    private val onboardingRepository: OnboardingRepository,
    private val jobRoleRepository: JobRoleRepository
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
                region = onboardingRequest.region,
                school = School.fromSchoolName(onboardingRequest.school),
                major = onboardingRequest.major
            )
            onboardingRepository.save(newOnboarding)
            member.completeOnboarding(newOnboarding)
            newOnboarding
        } else {
            val found = onboardingRepository.findById(onboardingId)
                .orElseThrow { NoSuchElementException("Onboarding not found: $onboardingId") }

            found.update(
                jobCategory = onboardingRequest.jobCategory,
                careerYear = onboardingRequest.careerYear,
                region = onboardingRequest.region,
                school = School.fromSchoolName(onboardingRequest.school),
                major = onboardingRequest.major
            )
            found
        }

        jobRoleRepository.deleteByOnboardingId(onboarding.id)

        val rolesToSave = onboardingRequest.jobRole
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .map { role -> JobRole.create(onboardingId = onboarding.id, jobRole = role) }

        if (rolesToSave.isNotEmpty()) {
            jobRoleRepository.saveAll(rolesToSave)
        }

    }
}