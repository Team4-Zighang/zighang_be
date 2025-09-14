package com.zighang.member.dto

import com.zighang.member.entity.JobRole
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding
import com.zighang.member.entity.value.Region

data class MemberDto(
    val member: MemberInfo,
    val onboarding: OnboardingInfo?,
    val jobRole: JobRoleInfo?,
) {
    companion object {
        fun create(member: Member, onboarding: Onboarding?, jobRoles: List<JobRole>?, regions : List<Region>?): MemberDto {
            return MemberDto(
                member = MemberInfo.create(member),
                onboarding = onboarding?.let { OnboardingInfo.create(it, regions) },
                jobRole = jobRoles?.let { JobRoleInfo.create(it) },
            )
        }
    }
}

data class MemberInfo (
    val memberId : Long,

    val memberName : String,

    val profileImageUrl : String?,

    val role: String
) {
    companion object{
        fun create(member: Member): MemberInfo{
            return MemberInfo(
                member.id,
                member.name,
                member.profileImageUrl,
                member.role.name
            )
        }
    }
}

data class OnboardingInfo (
    val jobCategory: String,

    val careerYear: Int,

    val region: List<Region>?,

    val school: String,

    val major: String
) {
    companion object{
        fun create(onboarding: Onboarding, regions : List<Region>?): OnboardingInfo{
            return OnboardingInfo(
                onboarding.jobCategory,
                onboarding.careerYear.year,
                regions,
                onboarding.school.schoolName,
                onboarding.major
            )
        }
    }
}

data class JobRoleInfo(
    val jobRole: List<String>,
) {
    companion object{
        fun create(jobRole: List<JobRole>): JobRoleInfo{
            return JobRoleInfo(
                jobRole.map {
                    it.jobRole
                }
            )
        }
    }
}