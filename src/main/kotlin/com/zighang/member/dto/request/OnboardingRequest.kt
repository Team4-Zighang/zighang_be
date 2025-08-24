package com.zighang.member.dto.request

import com.zighang.member.entity.value.*
import io.swagger.v3.oas.annotations.media.Schema

data class OnboardingRequest(
    @Schema(description = "캐릭터 이름", example = "DUMJIK_HAENG(\"듬직행\"),\n" +
            "SILSOK_HAENG(\"실속행\"),\n" +
            "SEONGSIL_HAENG(\"성실행\"),\n" +
            "DANJJAN_HAENG(\"단짠행\"),\n" +
            "MOHEOM_HAENG(\"모험행\"),\n" +
            "JJIN_DONG_HAENG(\"찐동행\"),\n" +
            "DOJEON_HAENG(\"도전행\"),\n" +
            "JAYU_HAENG(\"자유행\")")
    val characterName: CharacterName,

    @Schema(description = "기업 유형", example = "STABLE(\"안정형\"),\n" +
            "CHALLENGING(\"도전형\")")
    val companyType: CompanyType,

    @Schema(description = "근무 방식", example = "FIXED(\"매일 출근\"),\n" +
            "FLEXIBLE(\"유연 근무\")")
    val workType: WorkType,

    @Schema(description = "직업관", example = "COMPENSATION(\"연봉&복지\"),\n" +
            "GROWTH(\"성장지향\")")
    val viewOfJob: ViewOfJob,

    @Schema(description = "고용 형태", example = "FULL_TIME(\"정규직\"),\n" +
            "CONTRACT(\"계약/신입\")")
    val empType: EmpType
)
