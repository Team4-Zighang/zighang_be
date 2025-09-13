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

    @Enumerated(EnumType.STRING)
    @Column(name = "max_career_year", nullable = false)
    var maxCareerYear : CareerYear,

    @Enumerated(EnumType.STRING)
    @Column(name = "min_career_year", nullable = false)
    var minCareerYear : CareerYear,

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    var region : Region,

    @Column(name = "school", nullable = false)
    @Enumerated(EnumType.STRING)
    var school : School,

    @Column(name = "major", nullable = false)
    var major : String
) : BaseEntity() {
    companion object {
        fun create(
            jobCategory: String,
            maxCareerYear: CareerYear,
            minCareerYear: CareerYear,
            region: Region,
            school: School,
            major: String
        ): Onboarding {
            return Onboarding(
                jobCategory = jobCategory,
                maxCareerYear = maxCareerYear,
                minCareerYear = minCareerYear,
                region = region,
                school = school,
                major = major
            )
        }
    }

    fun update(
        jobCategory: String,
        maxCareerYear: CareerYear,
        minCareerYear: CareerYear,
        region: Region,
        school: School,
        major: String
    ) {
        this.jobCategory = jobCategory
        this.maxCareerYear = maxCareerYear
        this.minCareerYear = minCareerYear
        this.region = region
        this.school = school
        this.major = major
    }
}