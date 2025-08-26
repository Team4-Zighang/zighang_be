package com.zighang.memo.entity

import jakarta.persistence.*

@Entity
@Table(name = "memo")
class Memo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "posting_id")
    val postingId: Long,

    @Column(name = "member_id")
    val memberId: Long,

    @Column(name = "memo_content", columnDefinition = "TEXT")
    var memoContent: String,
) {
    companion object {
        fun create(
            postingId: Long,
            memberId: Long,
            memoContent: String,
        ): Memo {
            return Memo(
                postingId = postingId,
                memberId = memberId,
                memoContent = memoContent,
            )
        }
    }

    fun update(content: String) {
        this.memoContent = content
    }
}