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
    val charcterType: CharacterType,

    @Column(name = "company_size", nullable = false)
    @Enumerated(EnumType.STRING)
    val companySize: CompanySize,

    @Column(name = "company_size_value", nullable = false)
    val companySizeValue: Int,

    @Column(name = "work_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val workType: WorkType,

    @Column(name = "work_type_value", nullable = false)
    val workTypeValue: Int,

    @Column(name = "pursuit_of_value_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val pursuitOfValueType: PursuitOfValueType,

    @Column(name = "pursuit_of_value_type_value", nullable = false)
    val pursuitOfValueTypeValue: Int,
) {
}