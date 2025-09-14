package com.zighang.member.service

import com.zighang.member.entity.Major
import com.zighang.member.entity.value.School
import com.zighang.member.repository.MajorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MajorService(
    private val majorRepository: MajorRepository
) {
    @Transactional(readOnly = true)
    fun getBySchool(school: School) : List<Major> {
        return majorRepository.findAllBySchool(school)
    }

}