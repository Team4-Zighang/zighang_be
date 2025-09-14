package com.zighang.member.repository

import com.zighang.member.entity.Major
import com.zighang.member.entity.value.School
import org.springframework.data.jpa.repository.JpaRepository

interface MajorRepository : JpaRepository<Major, Long> {
    fun findAllBySchool(school: School) : List<Major>
}