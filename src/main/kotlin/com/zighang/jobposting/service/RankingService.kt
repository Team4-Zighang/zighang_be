package com.zighang.jobposting.service

import com.zighang.jobposting.entity.value.RankChange
import com.zighang.jobposting.repository.JobPostingRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RankingService(
    private val jobPostingRepository: JobPostingRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {

    @Scheduled(cron = "0 0 0/6 * * *")
    @Transactional
    fun updateJobPostingRankings() {

        val topJobPostingWithScores = redisTemplate.opsForZSet()
            .reverseRangeWithScores("scrap_ranking", 0, 99)

        val ids = topJobPostingWithScores?.map { it.value.toString().toLong() } ?: emptyList()
        val postingsById = jobPostingRepository.findAllById(ids).associateBy { it.id }
        val orderedPostings = ids.mapNotNull { postingsById[it] }

        orderedPostings.forEachIndexed { index, jobPosting ->
            jobPosting.changeLastRank(jobPosting.currentRank)
            jobPosting.changeCurrentRank(index + 1)
            jobPosting.changeRankChange(calculateRankChange(jobPosting.lastRank, jobPosting.currentRank))
        }

        jobPostingRepository.saveAll(orderedPostings)
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