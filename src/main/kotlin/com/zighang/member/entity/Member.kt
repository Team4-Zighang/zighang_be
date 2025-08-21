package com.zighang.member.entity

import com.zighang.core.infrastructure.jpa.shared.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "profile_image_url")
    var profileImageUrl: String?,
) : BaseEntity() {

    companion object {
        fun create(
            name: String,
            email: String,
            profileImageUrl: String?,
        ): Member {
            return Member(
                name = name,
                email = email,
                profileImageUrl = profileImageUrl ?: "",
            )
        }
    }
}