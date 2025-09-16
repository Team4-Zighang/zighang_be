package com.zighang.scrap.repository

import com.zighang.scrap.entity.Scrap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ScrapRepository : JpaRepository<Scrap, Long> {

    @Query(
        value = """
        SELECT scrap.*
        FROM scrap
        JOIN job_posting ON job_posting.id = scrap.posting_id
        WHERE scrap.member_id = :memberId
        ORDER BY
          CASE WHEN job_posting.recruitment_end_date < CURDATE() THEN 1 ELSE 0 END,
          scrap.created_at DESC,
          scrap.id DESC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM scrap 
        JOIN job_posting ON job_posting.id = scrap.posting_id
        WHERE scrap.member_id = :memberId
        """,
        nativeQuery = true
    )
    fun findAllByMemberId(memberId: Long, pageable: Pageable) : Page<Scrap>

    fun findByJobPostingIdAndMemberId(jobPostingId : Long, memberId : Long) : Scrap?

    fun findByIdAndMemberId(id : Long, memberId : Long) : Scrap?

    fun findByMemberIdIn(memberIdList: List<Long>): List<Scrap>

    fun findByMemberId(memberId: Long): List<Scrap>

    @Query("SELECT s.memberId FROM Scrap s WHERE s.memberId IN :memberIds GROUP BY s.memberId HAVING COUNT(s.memberId) >= 4")
    fun findMemberIdsWithMoreThanFourScraps(memberIds: List<Long>): List<Long>

    fun countByMemberId(memberId: Long) : Long

    fun findByMemberIdAndJobPostingId(memberId: Long, jobPostingId: Long) : Optional<Scrap>
}