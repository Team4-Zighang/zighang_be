package com.zighang.scrap.util

import com.zighang.jobposting.entity.JobPosting
import java.time.temporal.ChronoUnit

fun dDayFactory(jobPosting: JobPosting): String {
    if(jobPosting.recruitmentEndDate == null) return "상시"

    val today = java.time.LocalDate.now()
    val days = ChronoUnit.DAYS.between(today, jobPosting.recruitmentEndDate.toLocalDate())
    return if (days >= 0) "D-$days" else "마감"
}