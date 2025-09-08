package com.zighang.scrap.dto.response

import com.zighang.member.entity.Personality
import io.swagger.v3.oas.annotations.media.Schema

data class PersonalityAnalysisDto(

    @Schema(description = "캐릭터 이름", example = "듬직행")
    val characterName: String,

    val companyValue: CompanyValue,

    val workTypeValue: WorkTypeValue,

    val pursuitOfValue: PursuitOfValue
){
    companion object{
        fun create(personality: Personality): PersonalityAnalysisDto{
            return PersonalityAnalysisDto(
                characterName = personality.charcterType.displayName,
                CompanyValue(
                    personality.companySizeValue,
                    100 - personality.companySizeValue,
                ),
                WorkTypeValue(
                    personality.workTypeValue,
                    100 - personality.workTypeValue
                ),
                PursuitOfValue(
                    personality.pursuitOfValueTypeValue,
                    100 - personality.pursuitOfValueTypeValue
                )
            )
        }
    }
}

data class CompanyValue(

    @Schema(description = "대기업 수치", example = "72")
    val majorValue: Int,

    @Schema(description = "스타트업 수치", example = "28")
    val startUpValue: Int,
)

data class WorkTypeValue(

    @Schema(description = "출근 수치", example = "36")
    val officeValue: Int,

    @Schema(description = "원격 수치", example = "64")
    val remoteValue: Int,
)

data class PursuitOfValue(

    @Schema(description = "연봉 복지 수치", example = "27")
    val welfareFeeValue: Int,

    @Schema(description = "개인성장 수치", example = "73")
    val personalGrowthValue: Int,
)