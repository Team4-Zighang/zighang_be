package com.zighang.jobposting.service

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

        val jobPostings = jobPostingRepository.findAllById(
            topJobPostingWithScores?.map { it.value.toString().toLong() } ?: emptyList()
        )

        val rankingMap = jobPostings.associateBy { it.id }

        jobPostings.forEachIndexed { index, jobPosting ->
            jobPosting.changeLastRank(jobPosting.currentRank)
            jobPosting.changeCurrentRank(index)
            jobPosting.changeRankChange(calculateRankChange(jobPosting.lastRank, jobPosting.currentRank))
        }

        jobPostingRepository.saveAll(jobPostings)
    }

    private fun calculateRankChange(lastRank: Int, currentRank: Int): String {
        return when {
            lastRank == 0 -> "NEW" // 0이면 신규
            currentRank < lastRank -> "UP" // 순위 상승
            currentRank > lastRank -> "DOWN" // 순위 하락
            else -> "STABLE" // 순위 변동 없음
        }
    }
}