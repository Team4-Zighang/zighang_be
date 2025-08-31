package com.zighang.jobposting.service

import com.zighang.card.dto.CardRedis
import com.zighang.jobposting.repository.JobPostingRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class JobPostingService(
    private val jobPostingRepository: JobPostingRepository
) {
    fun top3ByJob(depthOne: String?, depthTwo: String?): List<CardRedis> {
        val page3 = PageRequest.of(0, 3)

        val first = jobPostingRepository.findTopJobPostingsByDepths(depthOne, depthTwo, page3)
        val firstIds = first.mapNotNull { it.id }

        // 1차로 3개가 끝나면 바로 CardRedis로 변환해서 반환
        if (firstIds.size == 3) {
            return firstIds.map { id -> CardRedis.create(jobPostingId = id, isOpen = false, openDateTime = null) }
        }

        // 부족분 보충
        val need = 3 - firstIds.size
        val second = if (need > 0) {
            jobPostingRepository.findTopJobPostingsExcludingIds(
                excluded = firstIds.isNotEmpty(),
                excludedIds = firstIds,
                pageable = PageRequest.of(0, need)
            )
        } else {
            emptyList()
        }

        // 합치고, 중복 제거, 최대 3개까지 자르고, CardRedis로 변환
        return (firstIds + second.mapNotNull { it.id })
            .distinct()
            .take(3)
            .map { id -> CardRedis.create(jobPostingId = id, isOpen = false, openDateTime = null) }
    }
}