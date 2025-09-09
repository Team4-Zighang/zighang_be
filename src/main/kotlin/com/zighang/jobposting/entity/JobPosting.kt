package com.zighang.jobposting.entity

import com.zighang.core.infrastructure.jpa.shared.BaseEntity
import com.zighang.jobposting.entity.value.Education
import com.zighang.jobposting.entity.value.RankChange
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "job_posting")
class JobPosting(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0L,

    @Column(name = "recruitment_original_url", columnDefinition = "TEXT")
    val recruitmentOriginalUrl: String = "",

    @Column(name = "ocr_data", columnDefinition = "TEXT")
    val ocrData: String = "",

    @Column(name = "summary_data", columnDefinition = "TEXT")
    val summaryData: String = "",

    @Column(name = "title", length = 1024)
    val title: String = "",

    @Column(name = "content", length = 1024)
    val content: String = "",

    // 추후 enum으로 데이터 보고 바꿀 것
    @Column(name = "recruitment_region")
    val recruitmentRegion: String = "",

    @Column(name = "recruitment_address", length = 512)
    val recruitmentAddress: String = "",

    @Column(name = "affiliate", length = 256)
    val affiliate: String = "",

    @Column(name = "recruitment_image_url", length = 512)
    val recruitmentImageUrl: String = "",

    @Column(name = "company", length = 1024)
    val company: String = "",

    // 나중에 enum으로 바꿀 것
    @Column(name = "education")
    @Enumerated(EnumType.STRING)
    val education: Education,

    // 나중에 enum으로 바꿀 것
    @Column(name = "industry")
    val industry: String = "",

    @Column(name = "recruitment_start_date")
    val recruitmentStartDate: LocalDateTime,

    @Column(name = "recruitment_end_date")
    val recruitmentEndDate: LocalDateTime,

    @Column(name = "upload_date")
    val uploadDate: LocalDateTime,

    // updatedAt

    // 나중에 enum으로 변경하기
    @Column(name = "recruitment_deadline_type")
    val recruitmentDeadlineType: String = "",

    // 나중에 enum으로 바꾸기
    @Column(name = "recruitment_type")
    val recruitmentType: String = "",

    @Column(name = "depth_one")
    val depthOne: String = "",

    @Column(name = "depth_two")
    val depthTwo: String = "",

    @Column(name = "job_description", columnDefinition = "TEXT")
    val jobDescription: String = "",

    @Column(name = "preferential_treatment", columnDefinition = "TEXT")
    val preferentialTreatment: String = "",

    @Column(name = "qualification", columnDefinition = "TEXT")
    val qualification: String = "",

    @Column(name = "recruitment_process", columnDefinition = "TEXT")
    val recruitmentProcess: String = "",

    @Column(name = "team_info")
    val teamInfo: String = "",

    @Column(name = "expired_date")
    val expiredDate: LocalDateTime,

    @Column(name = "career", nullable = true)
    var career: String,

    // 순위 산정 관련 column
    @Column(name = "current_rank", nullable = false)
    var currentRank: Int = 0,

    @Column(name="last_rank", nullable = false)
    var lastRank: Int = 0,

    @Column(name = "rank_change", nullable = false)
    @Enumerated(EnumType.STRING)
    var rankChange: RankChange = RankChange.NEW,

    @Column(name = "min_career")
    var minCareer:Int,

    @Column(name = "max_career")
    var maxCareer:Int,

    @Column(name = "view_count", nullable = false)
    var viewCount: Int = 100,

    @Column(name = "apply_count", nullable = false)
    var applyCount: Int = 100

) : BaseEntity() {

    fun changeLastRank(lastRank: Int) {
        this.lastRank = lastRank
    }

    fun changeCurrentRank(currentRank: Int) {
        this.currentRank = currentRank
    }

    fun changeRankChange(changeRank: RankChange) {
        this.rankChange = changeRank
    }

    fun updateViewCount() {
        this.viewCount += 1
    }

    fun updateApplyCount() {
        this.applyCount += 1
    }

    fun updateCareer(career: String) {
        this.career = career
    }
}