package com.zighang.scrap.repository

import com.zighang.scrap.entity.Scrap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ScrapRepository : JpaRepository<Scrap, Long> {
    fun findAllByMemberId(memberId : Long, pageable: Pageable) : Page<Scrap>

    fun findByJobPostingIdAndMemberId(jobPostingId : Long, memberId : Long) : Scrap?

    fun findByIdAndMemberId(id : Long, memberId : Long) : Scrap?

    fun findByMemberIdIn(memberIdList: List<Long>): List<Scrap>

    fun findByMemberId(memberId: Long): List<Scrap>

    @Query("SELECT s.memberId FROM Scrap s WHERE s.memberId IN :memberIds GROUP BY s.memberId HAVING COUNT(s.memberId) >= 4")
    fun findMemberIdsWithMoreThanFourScraps(memberIds: List<Long>): List<Long>

    fun countByMemberId(memberId: Long) : Long
}