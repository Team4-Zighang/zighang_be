package com.zighang.member.service

import com.zighang.member.entity.JobRole
import com.zighang.member.repository.JobRoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JobRoleService(
    private val jobRoleRepository: JobRoleRepository
) {
    @Transactional(readOnly = true)
    fun findAllByOnboardingId(onboardingId : Long) : List<JobRole> {
        return jobRoleRepository.findByOnboardingId(onboardingId)
    }
}