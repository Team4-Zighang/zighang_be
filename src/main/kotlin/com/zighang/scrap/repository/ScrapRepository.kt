package com.zighang.scrap.repository

import com.zighang.scrap.entity.Scrap
import org.springframework.data.jpa.repository.JpaRepository

interface ScrapRepository : JpaRepository<Scrap, Long> {
    fun findAllByMemberId(memberId : Long) : List<Scrap>
}