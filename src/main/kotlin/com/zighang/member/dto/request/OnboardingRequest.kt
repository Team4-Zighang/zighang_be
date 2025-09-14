package com.zighang.member.dto.request

import com.zighang.member.entity.value.*
import io.swagger.v3.oas.annotations.media.Schema

data class OnboardingRequest(
    @Schema(description = "직군", example = "IT")
    val jobCategory: String,

    @Schema(description = "직무", example = "[\"백엔드\", \"프론트엔드\"]")
    val jobRole: List<String>,

    @Schema(description = "연차", example = "YEAR_0(0),\n" +
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
    val careerYear: CareerYear,

    @Schema(description = "지역", example = "[\"SEOUL\", \"GYEONGGI\"]")
    val region: List<Region>,

    @Schema(description = "학교", example = "건국대학교")
    val school: String,

    @Schema(description = "전공", example = "현대")
    val major : String
)
