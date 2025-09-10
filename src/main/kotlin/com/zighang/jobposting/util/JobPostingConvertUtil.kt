package com.zighang.jobposting.util

import com.zighang.jobposting.entity.JobPosting
import com.zighang.member.entity.value.Region

fun getRegion(jobPosting: JobPosting) : String {
    val regions = jobPosting.recruitmentRegion.split(",")
        .mapNotNull { Region.entries.find { enumVal -> enumVal.name == it } }

    return regions.joinToString (", "){
        it.regionName
    }
}

fun getCareer(jobPosting: JobPosting) : String {
    return when {
        jobPosting.minCareer == -1 && jobPosting.maxCareer == -1 -> "무관"
        jobPosting.minCareer == 0 && jobPosting.maxCareer == 0 -> "신입"
        jobPosting.minCareer >= 3 && jobPosting.maxCareer <= 3 -> "경력 3년 이상"
        jobPosting.minCareer > 0 && jobPosting.maxCareer > 0 -> "경력 ${jobPosting.minCareer}-${jobPosting.maxCareer}년"
        else -> "기타"
    }
}