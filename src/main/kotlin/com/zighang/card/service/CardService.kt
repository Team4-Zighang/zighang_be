package com.zighang.card.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zighang.card.dto.*
import com.zighang.card.value.CardPosition
import com.zighang.core.exception.DomainException
import com.zighang.core.exception.GlobalErrorCode
import com.zighang.jobposting.entity.JobPosting
import com.zighang.jobposting.infrastructure.mapper.CompanyMapper
import com.zighang.jobposting.service.JobPostingService
import com.zighang.scrap.service.ScrapService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime


@Service
class CardService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val scrapService: ScrapService,
    private val companyMapper : CompanyMapper,
) {
    private val prefix = "top3JobPosting:"
    private fun servedKey(memberId: Long) = "card:served:$memberId"

    private fun key(memberId: Long) = "$prefix$memberId"
    private fun scrapKey(memberId: Long) = "scrap:$memberId"

    @Value("\${scrap.count_limit}")
    private lateinit var maxCount : String;
    @Value("\${cloudfront.url}")
    private lateinit var imageHost: String;

    fun getCardScrapCount(memberId: Long) : Long? {
        return redisTemplate.opsForValue().get(scrapKey(memberId))?.toLong()
    }

    fun upsertCardScrapCount(memberId: Long, scrapCount: Long) {
        redisTemplate.opsForValue().set(scrapKey(memberId), scrapCount.toString())
    }

    fun saveTop3Ids(memberId: Long, ids: List<CardRedis>) {
        require(ids.size == 3) { "Top3는 정확히 3개여야 합니다." }

        val withPos = ids.mapIndexed { idx, card ->
            val pos = when (idx) {
                0 -> CardPosition.LEFT
                1 -> CardPosition.CENTER
                else -> CardPosition.RIGHT
            }
            card.copy(position = pos)
        }

        val k = key(memberId)
        redisTemplate.delete(k)

        val values = withPos.map { objectMapper.writeValueAsString(it) }.toTypedArray()
        redisTemplate.opsForList().rightPushAll(k, *values)
    }

    fun getTop3Ids(memberId: Long): List<CardRedis> {
        return redisTemplate.opsForList().range(key(memberId), 0, -1)
            ?.map { objectMapper.readValue(it, CardRedis::class.java) }
            ?: emptyList()
    }

    fun getCardByPosition(memberId: Long, position: CardPosition): CardRedis {
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
        val index = list.indexOfFirst { it.position == position }
        if (index == -1) throw DomainException(GlobalErrorCode.NOT_FOUND_CARD)

        // 4) 값 갱신
        val updated = list[index].copy(
            isOpen = true,
            openDateTime = LocalDateTime.now(),
            cardJobPosting = CardJobPosting(
                list[index].jobPostingId,
                list[index].cardJobPosting!!.companyImageUrl,
                list[index].cardJobPosting!!.companyName,
                list[index].cardJobPosting!!.title,
                list[index].cardJobPosting!!.career,
                list[index].cardJobPosting!!.recruitmentType,
                list[index].cardJobPosting!!.academicConditions,
                list[index].cardJobPosting!!.address,
                scrapService.isScrap(memberId, list[index].jobPostingId)
            )
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
//        val mapper = jacksonObjectMapper()
//            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//            .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true) // ' 허용 (있으면)
//        println(jobPosting.company)
        val company = companyMapper.toJsonDto(jobPosting.company)
//        println(company)
//        println(company.companyName)
        val jobPostingId = jobPosting.id
        val title = jobPosting.title
        val career = parsingCareer(jobPosting.minCareer, jobPosting.maxCareer)
        val recruitmentType = jobPostingAnalysisDto.recruitmentType
        val academicConditions = jobPostingAnalysisDto.academicConditions
        val address = jobPosting.recruitmentAddress

        return CardJobPosting.create(
            jobPostingId!!,
                    imageHost + company.companyImageUrl,
            company.companyName,
            title,
            career,
            recruitmentType,
            academicConditions,
            address,
            false
        )
    }

    private fun parsingCareer(minCareer: Int, maxCareer: Int): String? {
        var careerString = ""
        if(minCareer == -1) {
            return "경력 무관"
        }
        if(minCareer == 0) {
            careerString += "신입"
            if(maxCareer == 0) {
                return careerString
            }
            if(maxCareer in 1..9) {
                careerString += (" ~ " + maxCareer.toString() + "년차")
                return careerString
            }
            if(maxCareer >= 10) {
                careerString += " ~ 10년차 이상"
                return careerString
            }
        }
        if(minCareer > 0) {
            careerString += (minCareer.toString() + "년차")
            if(maxCareer < 10) {
                careerString += (" ~ " + maxCareer.toString() + "년차")
                return careerString
            }
            else {
                careerString += " 이상"
                return careerString
            }
        }
        return "정보 없음"
    }

    fun getServedIds(memberId: Long): Set<Long> {
        val members = redisTemplate.opsForSet().members(servedKey(memberId)).orEmpty()
        return members.mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun idx(position: CardPosition) = when (position) {
        CardPosition.LEFT -> 0
        CardPosition.CENTER -> 1
        CardPosition.RIGHT -> 2
    }

    fun addServedId(memberId: Long, id: Long) {
        redisTemplate.opsForSet().add(servedKey(memberId), id.toString())
        redisTemplate.expire(servedKey(memberId), Duration.ofHours(1))
    }

    fun getOpenCardList(memberId: Long): List<CardContentResponse> {
        val now = LocalDateTime.now()                  // 시스템 타임존
        val cutoff = now.minusHours(6)

        // 1) 회원의 모든 카드 불러오기 (Top3만 쓰는 구조면 Top3, 별도 보관이 있으면 합쳐서 반환)
        val cards: List<CardRedis> = fetchAllCards(memberId)

        // 2) isOpen == true && openDateTime ∈ [cutoff, now] 인 카드만 필터
        return cards
            .asSequence()
            .filter { it.isOpen }
            .filter { it.openDateTime != null && !it.openDateTime!!.isBefore(cutoff) && !it.openDateTime!!.isAfter(now) }
            .sortedByDescending { it.openDateTime } // 최신순
            .map {
                val isScrap = scrapService.isScrap(memberId, it.cardJobPosting!!.jobPostingId)
                it.cardJobPosting!!.isScrap = isScrap
                CardContentResponse.from(it) }
            .toList()
    }

    private fun fetchAllCards(memberId: Long): List<CardRedis> {
        val k = key(memberId)
        val raw = redisTemplate.opsForList().range(k, 0, -1).orEmpty()
        return raw.map { objectMapper.readValue(it, CardRedis::class.java) }
    }

    fun getScrapForCard(memberId : Long): RemainScrapResponse {
        val dbCount = scrapService.getScrapCount(memberId)
        val redisCount = getCardScrapCount(memberId) ?: return RemainScrapResponse.create(0L)

        val diff = dbCount - redisCount
        return RemainScrapResponse.create(
            diff.coerceAtMost(maxCount.toLong())
        )
    }

    //memberId의 top3 카드 중 jobPostingId가 일치하는 카드 갱신
//    fun updateCardByJobPostingId(memberId: Long, jobPostingId: Long) {
//        val listKey = key(memberId)
//        val rawList: List<String> = redisTemplate.opsForList().range(listKey, 0, -1).orEmpty()
//
//        val mapper = jacksonObjectMapper()
//            .registerModule(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
//            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//
//        val mutableList = rawList.toMutableList()
//        val updates = mutableListOf<Pair<Int, String>>()
//
//        mutableList.forEachIndexed { index, json ->
//            val card = mapper.readValue(json, CardRedis::class.java)
//            if (card.jobPostingId == jobPostingId) {
//                val oldJobPosting = card.cardJobPosting
//                val newCardJobPosting = oldJobPosting?.copy(career = updatedCareer)
//                val updatedCard = card.copy(cardJobPosting = newCardJobPosting)
//                updates += index to mapper.writeValueAsString(updatedCard)
//            }
//        }
//
//        updates.forEach{ (idx, updatedJson) ->
//            redisTemplate.opsForList().set(listKey, idx.toLong(), updatedJson)
//        }
//    }

}