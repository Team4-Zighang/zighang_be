package com.zighang.card.service

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zighang.card.dto.CardJobPosting
import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.card.dto.CardRedis
import com.zighang.card.dto.Company
import com.zighang.jobposting.entity.JobPosting
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

    fun createCardJobPosting(jobPostingAnalysisDto: CardJobPostingAnalysisDto, jobPosting: JobPosting): CardJobPosting {
        val mapper = jacksonObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) // ' 허용 (있으면)
        val fixedJson = jobPosting.company
            .replace("'", "\"")                  // ' → "
            .replace(Regex("""\bNone\b"""), "null") // None → null
        val company: Company = mapper.readValue(fixedJson)
        val title = jobPosting.title
        val career = jobPostingAnalysisDto.career
        val recruitmentType = jobPostingAnalysisDto.recruitmentType
        val academicConditions = jobPostingAnalysisDto.academicConditions
        val address = jobPosting.recruitmentAddress

        return CardJobPosting.create(
            company.companyImageUrl,
            company.companyName,
            title,
            career,
            recruitmentType,
            academicConditions,
            address
        )
    }
}