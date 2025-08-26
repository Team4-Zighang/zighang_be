package com.zighang.member.service

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.member.dto.request.OnboardingRequest
import com.zighang.member.entity.Onboarding
import com.zighang.member.repository.OnboardingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OnboardingService(
    private val onboardingRepository: OnboardingRepository
) {
    @Transactional
    fun createOnboarding(onboardingRequest: OnboardingRequest) : Onboarding {
        val onboarding = Onboarding.create(
            onboardingRequest.jobCategory,
            onboardingRequest.jobRole,
            onboardingRequest.careerYear,
            onboardingRequest.region,
            onboardingRequest.school,
            onboardingRequest.targetCompany
        )
        return onboardingRepository.save(onboarding)
    }

    @Transactional(readOnly = true)
    fun findById(id : Long) : Onboarding? {
        return onboardingRepository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    fun getById(id : Long) : Onboarding {
        return findById(id) ?: throw DomainException(GlobalErrorCode.NOT_EXIST_ONBOARDING)
    }

    @Transactional
    fun updateOnboarding(onboarding: Onboarding, onboardingRequest: OnboardingRequest) {
        onboarding.update(
            onboardingRequest.jobCategory,
            onboardingRequest.jobRole,
            onboardingRequest.careerYear,
            onboardingRequest.region,
            onboardingRequest.school,
            onboardingRequest.targetCompany
        )
    }
}