package com.zighang.member.entity

import com.zighang.member.entity.value.personality.CharacterType
import com.zighang.member.entity.value.personality.CompanySize
import com.zighang.member.entity.value.personality.PursuitOfValueType
import com.zighang.member.entity.value.personality.WorkType
import jakarta.persistence.*

@Entity
@Table(name = "personality")
class Personality(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "member_id", nullable = false)
    val memberId: Long,

    @Column(name = "charcter_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var characterType: CharacterType,

    @Column(name = "company_size", nullable = false)
    @Enumerated(EnumType.STRING)
    var companySize: CompanySize,

    @Column(name = "company_size_value", nullable = false)
    var companySizeValue: Int,

    @Column(name = "work_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var workType: WorkType,

    @Column(name = "work_type_value", nullable = false)
    var workTypeValue: Int,

    @Column(name = "pursuit_of_value_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var pursuitOfValueType: PursuitOfValueType,

    @Column(name = "pursuit_of_value_type_value", nullable = false)
    var pursuitOfValueTypeValue: Int,
) {

    companion object{
        fun create(
            memberId: Long,
            characterType: CharacterType,
            companySize: CompanySize,
            companySizeValue: Int,
            workType: WorkType,
            workTypeValue: Int,
            pursuitOfValueType: PursuitOfValueType,
            pursuitOfValueTypeValue: Int,
        ): Personality {
            return Personality(
                memberId = memberId,
                characterType = characterType,
                companySize = companySize,
                companySizeValue = companySizeValue,
                workType = workType,
                workTypeValue = workTypeValue,
                pursuitOfValueType = pursuitOfValueType,
                pursuitOfValueTypeValue = pursuitOfValueTypeValue
            )
        }
    }

    fun update(
        charcterType: CharacterType,
        companySize: CompanySize,
        companySizeValue: Int,
        workType: WorkType,
        workTypeValue: Int,
        pursuitOfValueType: PursuitOfValueType,
        pursuitOfValueTypeValue: Int,
    ) {
        this.characterType = charcterType
        this.companySize = companySize
        this.companySizeValue = companySizeValue
        this.workType = workType
        this.workTypeValue = workTypeValue
        this.pursuitOfValueType = pursuitOfValueType
        this.pursuitOfValueTypeValue = pursuitOfValueTypeValue
    }
}