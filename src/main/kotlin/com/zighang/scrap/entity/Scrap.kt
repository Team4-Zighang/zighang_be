package com.zighang.scrap.entity

import com.zighang.core.infrastructure.jpa.shared.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "scrap")
class Scrap(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(name = "posting_id")
    var jobPostingId: Long,

    @Column(name = "member_id")
    val memberId : Long,

    @Column(name = "resume_url", length = 1024)
    var resumeUrl : String?,

    @Column(name = "portfolio_url", length = 1024)
    var portfolioUrl : String?
) : BaseEntity() {
    companion object {
        fun create(jobPostingId: Long, memberId: Long,
                   resumeUrl: String?, portfolioUrl: String?) : Scrap {
            return Scrap(
                jobPostingId = jobPostingId,
                memberId = memberId,
                resumeUrl = resumeUrl,
                portfolioUrl = portfolioUrl
            )
        }
    }

    fun update(postingId: Long, resumeUrl: String?, portfolioUrl: String?) {
        this.jobPostingId = postingId
        this.resumeUrl = resumeUrl
        this.portfolioUrl = portfolioUrl
    }
}