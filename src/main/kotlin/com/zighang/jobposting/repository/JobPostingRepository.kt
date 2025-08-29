package com.zighang.jobposting.repository

import com.zighang.jobposting.entity.JobPosting
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JobPostingRepository : CrudRepository<JobPosting, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE JobPosting j SET j.qualification = :qualification, j.preferentialTreatment = :preferentialTreatment WHERE j.id = :id")
    fun updateJobPostingAnalysis(id: Long, qualification: String, preferentialTreatment: String)
}