package com.zighang.jobposting.repository

import com.zighang.jobposting.entity.JobPosting
import com.zighang.member.entity.value.School
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
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
            SET j.qualification = :qualification, j.preferentialTreatment = :preferentialTreatment, j.career = :career 
            WHERE j.id = :id
        """
    )
    fun updateJobPostingAnalysis(
        @Param("id") id: Long,
        @Param("qualification") qualification: String,
        @Param("preferentialTreatment") preferentialTreatment: String,
        @Param("career") career: String,
    ) : Int
    // 1) 직무/직군 조건 + 스크랩 수 내림차순 (Top N: Pageable)
    @Query(
        """
        select jp
        from JobPosting jp
        left join Scrap s on s.jobPostingId = jp.id
        where (:depthOne is null or jp.depthOne = :depthOne)
          and (:depthTwoList is null or jp.depthTwo in :depthTwoList)
        group by jp
        order by count(s.id) desc, jp.uploadDate desc, jp.id desc
        """
    )
    fun findTopJobPostingsByDepths(
        @Param("depthOne") depthOne: String?,
        @Param("depthTwoList") depthTwoList: List<String>?,
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

    @Query("""
        SELECT jp
        FROM JobPosting jp
        LEFT JOIN Scrap s ON s.jobPostingId = jp.id
        WHERE (:excluded = false OR jp.id NOT IN :excludedIds)
          AND (:depthOne IS NULL OR jp.depthOne = :depthOne)
          AND (:depthTwo IS NULL OR jp.depthTwo = :depthTwo)
        GROUP BY jp.id
        ORDER BY COUNT(s.id) DESC, jp.uploadDate DESC, jp.id DESC
    """)
    fun findNextByExcludingIdsAndDepths(
        @Param("excluded") excluded: Boolean,
        @Param("excludedIds") excludedIds: List<Long>,
        @Param("depthOne") depthOne: String?,
        @Param("depthTwo") depthTwo: String?,
        pageable: Pageable
    ): List<JobPosting>

    @Query("""
        SELECT jp
        FROM JobPosting jp
        LEFT JOIN Scrap s ON s.jobPostingId = jp.id
        WHERE (:excluded = false OR jp.id NOT IN :excludedIds)
        GROUP BY jp.id
        ORDER BY COUNT(s.id) DESC, jp.uploadDate DESC, jp.id DESC
    """)
    fun findNextByExcludingIdsOrderByPopularity(
        @Param("excluded") excluded: Boolean,
        @Param("excludedIds") excludedIds: List<Long>,
        pageable: Pageable
    ): List<JobPosting>

    @Query(
        value = """
            SELECT DISTINCT j 
            FROM JobPosting j
            INNER JOIN Scrap s ON j.id = s.jobPostingId
            INNER JOIN Member m ON s.memberId = m.id
            INNER JOIN Onboarding o ON m.onboardingId = o.id
            INNER JOIN JobRole jr ON jr.onboardingId = o.id
            WHERE o.school = :school 
              AND jr.jobRole IN :jobRoles
            ORDER BY j.uploadDate DESC
        """,
        countQuery = """
            SELECT COUNT(DISTINCT j.id) 
            FROM JobPosting j
            INNER JOIN Scrap s ON j.id = s.jobPostingId
            INNER JOIN Member m ON s.memberId = m.id
            INNER JOIN Onboarding o ON m.onboardingId = o.id
            INNER JOIN JobRole jr ON jr.onboardingId = o.id
            WHERE o.school = :school 
              AND jr.jobRole IN :jobRoles
        """
    )
    fun findAllScrappedJobPostingsBySimilarUsers(
        school: School,
        jobRoles: List<String>,
        pageable: Pageable
    ): Page<JobPosting>

    @Query("""
        SELECT j 
        FROM JobPosting j
        INNER JOIN Scrap s ON j.id = s.jobPostingId
        INNER JOIN Member m ON s.memberId = m.id
        INNER JOIN Onboarding o ON m.onboardingId = o.id
        INNER JOIN JobRole jr ON jr.onboardingId = o.id
        WHERE o.school = :school 
          AND jr.jobRole IN :jobRoles
        GROUP BY j.id
        ORDER BY COUNT(s.id) DESC
    """)
    fun findTop3ScrappedJobPostingsBySimilarUsers(
        school: School,
        jobRoles: List<String>,
        pageable: Pageable = PageRequest.of(0, 3)
    ): List<JobPosting>

    @Query("""
        select jp
        from JobPosting jp
        left join Scrap s on s.jobPostingId = jp.id
        where (jp.depthOne = :depthOne)
          and (jp.id in :postingIds)
          and (:memberId = s.memberId)
        group by jp order by max(s.createdAt) desc
    """)
    fun findScrapedJobPostingsBydepthOneAndMemberId(
        @Param("memberId") memberId : Long,
        @Param("postingIds") postingIds: List<Long>,
        @Param("depthOne") depthOne: String,
        pageable: Pageable
    ) : List<JobPosting>

    @Query("""
        select jp
        from JobPosting jp
        left join Scrap s on s.jobPostingId = jp.id
        where (jp.id in :postingIds)
          and (:memberId = s.memberId)
        group by jp order by max(s.createdAt) desc
    """)
    fun findScrapedJobPostingsByMemberIdAndJobPostingIds(
        @Param("memberId") memberId : Long,
        @Param("postingIds") postingIds: List<Long>,
        pageable: Pageable
    ) : List<JobPosting>
}