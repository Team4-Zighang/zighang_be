package com.zighang.jobposting.repository

import com.zighang.jobposting.entity.JobPosting
import com.zighang.member.entity.value.School
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JobPostingRepository : CrudRepository<JobPosting, Long> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
            UPDATE JobPosting j 
            SET j.qualification = :qualification, j.preferentialTreatment = :preferentialTreatment 
            WHERE j.id = :id
        """
    )
    fun updateJobPostingAnalysis(
        @Param("id") id: Long,
        @Param("qualification") qualification: String,
        @Param("preferentialTreatment") preferentialTreatment: String,
    ) : Int

    @Query("""
        SELECT j FROM JobPosting j 
        INNER JOIN Scrap s ON j.id = s.jobPostingId
        INNER JOIN Member m ON s.memberId = m.id
        INNER JOIN Onboarding o ON m.onboardingId = o.id
        WHERE o.school = :school AND o.jobRole = :jobRole
    """)
    fun findAllScrappedJobPostingsBySimilarUsers(
        school: School,
        jobRole: String,
        pageable: Pageable
    ): Page<JobPosting>
}