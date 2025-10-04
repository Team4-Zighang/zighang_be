package com.zighang.jobposting.service

import com.zighang.jobposting.entity.value.RankChange
import com.zighang.jobposting.repository.JobPostingRepository
import com.zighang.member.entity.JobRole
import com.zighang.member.entity.value.School
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RankingService(
    private val jobPostingRepository: JobPostingRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object{
        const val GLOBAL_RANKING_KEY = "scrap_ranking"
        fun getSimilarUserRankingKey(school: School, jobRoles: List<JobRole>): String {
            val sortedJobRoles = jobRoles.map { it.jobRole }.sorted().joinToString(",")
            return "scrap_ranking:${school.schoolName}:$sortedJobRoles"
        }
    }

    @Scheduled(cron = "0 0 0/6 * * *")
    @Transactional
    fun updateJobPostingRankings() {
        updateRankingFor(GLOBAL_RANKING_KEY)
    }

    fun increamentScore(rankingKey: String, jobPostingId: Long){
        redisTemplate.opsForZSet().incrementScore(rankingKey, jobPostingId.toString(), 1.0)
    }

    fun getTopRankedJobPostingIds(rankingKey: String, topN: Long): List<Long> {
        return redisTemplate.opsForZSet().reverseRange(rankingKey, 0, topN - 1)
            ?.map { it.toString().toLong() }?: emptyList()
    }

    private fun updateRankingFor(rankingKey: String) {
        val topJobPostingWithScores = redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, 0, 99)

        val ids = topJobPostingWithScores?.map{ it.value.toString().toLong() } ?: return
        if(ids.isEmpty()) return

        val postingsById = jobPostingRepository.findAllById(ids).associateBy { it.id }
        val orderedPostings = ids.mapNotNull{ postingsById[it] }

        orderedPostings.forEachIndexed{ index, jobPosting ->
            if(rankingKey == GLOBAL_RANKING_KEY) {
                jobPosting.changeLastRank(jobPosting.currentRank)
                jobPosting.changeCurrentRank(index + 1)
                jobPosting.changeRankChange(calculateRankChange(jobPosting.lastRank, jobPosting.currentRank))
            }
        }

        if(rankingKey == GLOBAL_RANKING_KEY) {
            jobPostingRepository.saveAll(orderedPostings)
        }
    }

    private fun calculateRankChange(lastRank: Int, currentRank: Int): RankChange {
        return when {
            lastRank == 0 -> RankChange.NEW // 0이면 신규
            currentRank < lastRank -> RankChange.UP // 순위 상승
            currentRank > lastRank -> RankChange.DOWN // 순위 하락
            else -> RankChange.STABLE // 순위 변동 없음
        }
    }
}