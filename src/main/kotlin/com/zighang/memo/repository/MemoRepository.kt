package com.zighang.memo.repository

import com.zighang.memo.entity.Memo
import org.springframework.data.repository.CrudRepository

interface MemoRepository : CrudRepository<Memo, Long> {

    fun findByPostingIdAndMemberId(postingId: Long, memberId: Long): Memo?
}