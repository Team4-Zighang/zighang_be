package com.zighang.jobposting.util

import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.entity.value.RecruitmentType
import com.zighang.member.entity.value.Region
import java.time.LocalDateTime
import java.time.ZoneId

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


fun getWorkType(jobPosting: JobPosting): String {
    val workTypes = jobPosting.recruitmentType.split(",")
        .mapNotNull { RecruitmentType.entries.find { enumVal -> enumVal.name == it } }

    return workTypes.joinToString(", ") {
        it.displayName
    }
}

fun formatPostingDate(date: LocalDateTime?, type: String): String {
    if (date == null) return if (type == "end") "상시채용" else "수시채용"

    // 한국 시간 기준
    val koreanDate = date.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime()

    val month = koreanDate.monthValue
    val day = koreanDate.dayOfMonth
    val hour = koreanDate.hour
    val minute = koreanDate.minute

    return when (type) {
        "start" -> "${month}월 ${day}일 게시"
        "end" -> "${month}월 ${day}일 ${hour.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')} 마감"
        else -> ""
    }
}