package com.zighang.jobposting.repository

import com.zighang.jobposting.entity.PostingEvaluation
import com.zighang.member.entity.value.School
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface PostingEvaluationRepository : CrudRepository<PostingEvaluation, Long> {

    @Query("""
        select p
        from PostingEvaluation p  
        inner join Member m on m.id = p.memberId
        inner join Onboarding o on o.id = m.onboardingId
        where p.postingId = :postingId
        and o.school = :school
        order by p.createdAt desc
    """)
    fun findByPostingIdAndSchool(postingId: Long, school: School): List<PostingEvaluation>
}