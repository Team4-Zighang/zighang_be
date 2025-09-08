package com.zighang.member.entity

import com.zighang.core.infrastructure.jpa.shared.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "jobRole")
class JobRole(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "onboarding_id")
    var onboardingId : Long,

    @Column(name = "job_role")
    var jobRole : String
) : BaseEntity() {
    companion object {
        fun create(
            onboardingId: Long,
            jobRole: String
        ) : JobRole {
            return JobRole(
                onboardingId = onboardingId,
                jobRole =  jobRole
            )
        }
    }
}