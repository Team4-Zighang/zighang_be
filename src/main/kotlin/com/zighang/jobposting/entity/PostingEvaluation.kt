package com.zighang.jobposting.entity

import com.zighang.jobposting.entity.value.RecruitmentStep
import jakarta.persistence.*

@Entity
@Table(name = "posting_evaluation")
class PostingEvaluation (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "member_id", nullable = false)
    val memberId : Long,

    @Column(name = "posting_id", nullable = false)
    val postingId : Long,

    @Column(name = "eval_score", nullable = false)
    val evalScore : Int,

    @Column(name = "eval_text", nullable = false)
    val evalText : String,

    @Column(name = "recruitment_step", nullable = false)
    @Enumerated(EnumType.STRING)
    val recruitmentStep : RecruitmentStep,
){

    companion object{
        fun create(
            memberId: Long,
            postingId: Long,
            evalScore: Int,
            evalText: String,
            recruitmentStep: RecruitmentStep,
        ): PostingEvaluation{
            return PostingEvaluation(
                memberId = memberId,
                postingId = postingId,
                evalScore = evalScore,
                evalText = evalText,
                recruitmentStep = recruitmentStep,
            )
        }
    }
}