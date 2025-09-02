package com.zighang.member.entity

import com.zighang.core.infrastructure.jpa.shared.BaseEntity
import com.zighang.member.entity.value.*
import jakarta.persistence.*

@Entity
@Table(name = "onboarding")
class Onboarding (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "job_category", nullable = false)
    var jobCategory : String,

    @Column(name = "job_role", nullable = false)
    var jobRole : String,

    @Enumerated(EnumType.STRING)
    @Column(name = "career_year", nullable = false)
    var careerYear : CareerYear,

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    var region : Region,

    @Column(name = "school", nullable = false)
    @Enumerated(EnumType.STRING)
    var school : School,

    @Column(name = "target_company", nullable = false)
    var targetCompany : String,
) : BaseEntity() {
    companion object {
        fun create(
            jobCategory: String,
            jobRole: String,
            careerYear: CareerYear,
            region: Region,
            school: School,
            targetCompany: String
        ): Onboarding {
            return Onboarding(
                jobCategory = jobCategory,
                jobRole = jobRole,
                careerYear = careerYear,
                region = region,
                school = school,
                targetCompany = targetCompany
            )
        }
    }

    fun update(
        jobCategory: String,
        jobRole: String,
        careerYear: CareerYear,
        region: Region,
        school: School,
        targetCompany: String
    ) {
        this.jobCategory = jobCategory
        this.jobRole = jobRole
        this.careerYear = careerYear
        this.region = region
        this.school = school
        this.targetCompany = targetCompany
    }
}