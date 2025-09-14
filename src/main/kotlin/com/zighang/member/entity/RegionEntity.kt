package com.zighang.member.entity

import com.zighang.core.infrastructure.jpa.shared.BaseEntity
import com.zighang.member.entity.value.Region
import jakarta.persistence.*

@Entity
@Table(name = "region_entity")
class RegionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "onboarding_id")
    var onboardingId : Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    var region : Region
) : BaseEntity(){

    companion object {
        fun create(
            onboardingId: Long,
            region: Region
        ) : RegionEntity{
            return RegionEntity(
                onboardingId = onboardingId,
                region = region
            )
        }
    }
    fun update(
        onboardingId: Long,
        region: Region
    ) {
        this.onboardingId = onboardingId
        this.region = region
    }
}