package com.zighang.member.entity

import com.zighang.core.infrastructure.jpa.shared.BaseEntity
import com.zighang.member.entity.value.School
import jakarta.persistence.*

@Entity
@Table(name = "major")
class Major(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(name = "school")
    val school : School,

    @Column(name = "major_name")
    val majorName : String
) : BaseEntity() {
    
}
