package com.zighang.scrap.util

import com.zighang.jobposting.entity.JobPosting
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun dDayFactory(jobPosting: JobPosting): String {
    val dday: String = if (jobPosting.recruitmentEndDate == null) {
        "상시"
    } else if(jobPosting.recruitmentEndDate.isAfter(LocalDateTime.now())) {
        val ddayCount = ChronoUnit.DAYS.between(LocalDateTime.now(), jobPosting.recruitmentEndDate)
        "D-$ddayCount"
    } else {
        "마감"
    }

    return dday
}