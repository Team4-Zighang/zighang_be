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
    @Column(name = "career_year", nullable = false)
    var careerYear : CareerYear,

    @Column(name = "school", nullable = false)
    @Enumerated(EnumType.STRING)
    var school : School,

    @Column(name = "major", nullable = false)
    var major : String
) : BaseEntity() {
    companion object {
        fun create(
            jobCategory: String,
            careerYear: CareerYear,
            school: School,
            major: String
        ): Onboarding {
            return Onboarding(
                jobCategory = jobCategory,
                careerYear = careerYear,
                school = school,
                major = major
            )
        }
    }

    fun update(
        jobCategory: String,
        careerYear: CareerYear,
        school: School,
        major: String
    ) {
        this.jobCategory = jobCategory
        this.careerYear = careerYear
        this.school = school
        this.major = major
    }
}