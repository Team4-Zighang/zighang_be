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

    @Enumerated(EnumType.STRING)
    @Column(name = "character_name", nullable = false)
    var characterName : CharacterName,

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", nullable = false)
    var companyType : CompanyType,

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", nullable = false)
    var workType : WorkType,

    @Enumerated(EnumType.STRING)
    @Column(name = "view_of_job", nullable = false)
    var viewOfJob : ViewOfJob,

    @Enumerated(EnumType.STRING)
    @Column(name = "emp_type", nullable = false)
    var empType : EmpType,

    @Enumerated(EnumType.STRING)
    @Column(name = "school")
    var school: School?,

    @Enumerated(EnumType.STRING)
    @Column(name = "major")
    var major: Major?
) : BaseEntity() {
    companion object {
        fun create(
            characterName: CharacterName,
            companyType : CompanyType,
            workType: WorkType,
            viewOfJob: ViewOfJob,
            empType : EmpType
        ): Onboarding {
            return Onboarding(
                characterName = characterName,
                companyType = companyType,
                workType = workType,
                viewOfJob = viewOfJob,
                empType = empType,
                school = null,
                major = null
            )
        }
    }

    fun updateCharacter(
        characterName: CharacterName,
        companyType : CompanyType,
        workType: WorkType,
        viewOfJob: ViewOfJob,
        empType : EmpType
    ) {
        this.characterName = characterName
        this.companyType = companyType
        this.workType = workType
        this.viewOfJob = viewOfJob
        this.empType = empType
    }

    fun updateEducationInfo(
        school: School,
        major: Major
    ) {
        this.school = school
        this.major = major
    }
}