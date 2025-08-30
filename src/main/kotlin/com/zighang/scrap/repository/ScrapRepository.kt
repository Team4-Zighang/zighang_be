package com.zighang.scrap.repository

import com.zighang.scrap.entity.Scrap
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ScrapRepository : JpaRepository<Scrap, Long> {
    fun findAllByMemberId(memberId : Long, pageable: Pageable) : Page<Scrap>

    fun findByJobPostingIdAndMemberId(jobPostingId : Long, memberId : Long) : Scrap?
}