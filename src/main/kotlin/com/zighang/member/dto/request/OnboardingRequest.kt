package com.zighang.member.dto.request

import com.zighang.member.entity.value.*
import io.swagger.v3.oas.annotations.media.Schema

data class OnboardingRequest(
    @Schema(description = "직군", example = "IT")
    val jobCategory: String,

    @Schema(description = "직무", example = "[\"백엔드\", \"프론트엔드\"]")
    val jobRole: List<String>,

    @Schema(description = "최소 연차", example = "YEAR_0(0),\n" +
            "    YEAR_1(1),\n" +
            "    YEAR_2(2),\n" +
            "    YEAR_3(3),\n" +
            "    YEAR_4(4),\n" +
            "    YEAR_5(5),\n" +
            "    YEAR_6(6),\n" +
            "    YEAR_7(7),\n" +
            "    YEAR_8(8),\n" +
            "    YEAR_9(9),\n" +
            "    YEAR_10_PLUS(10),")
    val minCareerYear: CareerYear,

    @Schema(description = "최대 연차", example = "YEAR_0(0),\n" +
            "    YEAR_1(1),\n" +
            "    YEAR_2(2),\n" +
            "    YEAR_3(3),\n" +
            "    YEAR_4(4),\n" +
            "    YEAR_5(5),\n" +
            "    YEAR_6(6),\n" +
            "    YEAR_7(7),\n" +
            "    YEAR_8(8),\n" +
            "    YEAR_9(9),\n" +
            "    YEAR_10_PLUS(10),")
    val maxCareerYear: CareerYear,

    @Schema(description = "지역", example = "SEOUL(서울),\n" +
            "    GYEONGGI(경기),\n" +
            "    INCHEON(인천),\n" +
            "    BUSAN(부산),\n" +
            "    DAEGU(대구),\n" +
            "    GWANGJU(광주),\n" +
            "    DAEJEON(대전),\n" +
            "    ULSAN(울산),\n" +
            "    SEJONG(세종),\n" +
            "    GANGWON(강원),\n" +
            "    GYEONGNAM(경남),\n" +
            "    GYEONGBUK(경북),\n" +
            "    JEONNAM(전남),\n" +
            "    JEONBUK(전북),\n" +
            "    CHUNGNAM(충남),\n" +
            "    CHUNGBUK(충북),\n" +
            "    JEJU(제주),\n" +
            "    OVERSEAS(해외),\n" +
            "    OTHERS(기타)")
    val region: Region,

    @Schema(description = "학교", example = "건국대학교")
    val school: String,

    @Schema(description = "전공", example = "현대")
    val major : String
)
