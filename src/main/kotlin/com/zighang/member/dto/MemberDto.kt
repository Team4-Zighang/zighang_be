package com.zighang.member.dto

import com.zighang.member.entity.JobRole
import com.zighang.member.entity.Member
import com.zighang.member.entity.Onboarding

data class MemberDto (

    val member : Member,

    val onboarding: Onboarding?,

    val jobRole: List<JobRole>?
)
