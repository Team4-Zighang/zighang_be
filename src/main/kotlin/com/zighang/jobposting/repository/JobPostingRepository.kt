package com.zighang.jobposting.repository

import com.zighang.jobposting.entity.JobPosting
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JobPostingRepository : CrudRepository<JobPosting, Long> {
}