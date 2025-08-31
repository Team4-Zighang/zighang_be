package com.zighang.card.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.card.dto.CardRedis
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class CardService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val prefix = "top3JobPosting:"

    private fun key(memberId: Long) = "$prefix$memberId"

    // 저장: jobPostingId 리스트를 String으로 저장
    fun saveTop3Ids(memberId: Long, ids: List<CardRedis>) : List<Long> {
        if (ids.isNotEmpty()) {
            val values = ids.map { objectMapper.writeValueAsString(it) }.toTypedArray()
            redisTemplate.opsForList().rightPushAll(key(memberId), *values)
        }
        return ids.map { it.cardId }
    }

    fun getTop3Ids(memberId: Long): List<CardRedis> {
        return redisTemplate.opsForList().range(key(memberId), 0, -1)
            ?.map { objectMapper.readValue(it, CardRedis::class.java) }
            ?: emptyList()
    }

    fun evict(memberId: Long) {
        redisTemplate.delete(key(memberId))
    }
}