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
    // 1) 직무/직군 조건 + 스크랩 수 내림차순 (Top N: Pageable)
    @Query(
        """
        select jp
        from JobPosting jp
        left join Scrap s on s.jobPostingId = jp.id
        where (:depthOne is null or jp.depthOne = :depthOne)
          and (:depthTwo is null or jp.depthTwo = :depthTwo)
        group by jp
        order by count(s.id) desc, jp.uploadDate desc, jp.id desc
        """
    )
    fun findTopJobPostingsByDepths(
        @Param("depthOne") depthOne: String?,
        @Param("depthTwo") depthTwo: String?,
        pageable: Pageable
    ): List<JobPosting>

    // 2) 제외할 공고를 빼고 전체에서 스크랩 순으로 (Top N: Pageable)
    @Query(
        """
        select jp
        from JobPosting jp
        left join Scrap s on s.jobPostingId = jp.id
        where (:excluded = false or jp.id not in :excludedIds)
        group by jp
        order by count(s.id) desc, jp.uploadDate desc, jp.id desc
        """
    )
    fun findTopJobPostingsExcludingIds(
        @Param("excluded") excluded: Boolean,          // excludedIds가 비었으면 false로
        @Param("excludedIds") excludedIds: List<Long>, // 비었을 수도 있음
        pageable: Pageable
    ): List<JobPosting>

    @Query(
        value = """
            SELECT DISTINCT j FROM JobPosting j
            INNER JOIN Scrap s ON j.id = s.jobPostingId
            INNER JOIN Member m ON s.memberId = m.id
            INNER JOIN Onboarding o ON m.onboardingId = o.id
            WHERE o.school = :school AND o.jobRole = :jobRole
            ORDER BY j.uploadDate DESC
        """,
        countQuery = """
            SELECT COUNT(DISTINCT j.id) FROM JobPosting j
            INNER JOIN Scrap s ON j.id = s.jobPostingId
            INNER JOIN Member m ON s.memberId = m.id
            INNER JOIN Onboarding o ON m.onboardingId = o.id
            WHERE o.school = :school AND o.jobRole = :jobRole
        """
    )
    fun findAllScrappedJobPostingsBySimilarUsers(
        school: School,
        jobRole: String,
        pageable: Pageable
    ): Page<JobPosting>

    @Query("""
        SELECT j FROM JobPosting j
        INNER JOIN Scrap s on j.id = s.jobPostingId
        INNER JOIN Member m ON s.memberId = m.id
        INNER JOIN Onboarding o ON m.onboardingId = o.id
        where o.school= :school AND o.jobRole = :jobRole
        group by (j.id)
        order by count(s.id) desc
        limit 3
    """)
    fun findTop3ScrappedJobPostingsBySimilarUsers(
        school: School,
        jobRole: String,
    ): List<JobPosting>
}