package com.zighang.card.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zighang.card.dto.CardJobPosting
import com.zighang.card.dto.CardJobPostingAnalysisDto
import com.zighang.card.dto.CardRedis
import com.zighang.card.dto.Company
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.jobposting.entity.JobPosting
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime


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

    fun getCardById(memberId: Long, cardId : Long): CardRedis {
        val key = key(memberId)

        // 1) 리스트 전체 읽기 (각 원소는 JSON 문자열)
        val rawList: List<String> = redisTemplate.opsForList().range(key, 0, -1)
            ?: throw DomainException(GlobalErrorCode.NOT_EXIST_MEMBER_CARD)

        val mapper = jacksonObjectMapper()
            .registerModule(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()) // LocalDateTime
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        // 2) 각 원소를 CardRedis로 파싱
        val list: MutableList<CardRedis> = rawList
            .map { mapper.readValue<CardRedis>(it) }
            .toMutableList()

        // 3) 특정 cardId 찾기
        val index = list.indexOfFirst { it.cardId == cardId }
        if (index == -1) throw DomainException(GlobalErrorCode.NOT_FOUND_CARD)

        // 4) 값 갱신
        val updated = list[index].copy(
            isOpen = true,
            openDateTime = LocalDateTime.now()
        )
        list[index] = updated

        // 5) 해당 인덱스만 Redis에 다시 저장 (List 자료구조 유지)
        val updatedJson = mapper.writeValueAsString(updated)
        redisTemplate.opsForList().set(key, index.toLong(), updatedJson)

        return updated
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