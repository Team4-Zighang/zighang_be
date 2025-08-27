package com.zighang.memo.exception

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

enum class MemoErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String,
) : BaseErrorCode<DomainException> {

    NOT_EXIST_POSTING(HttpStatus.NOT_FOUND, "해당 공고가 존재하지 않습니다.");

    override fun toException(): DomainException {
        return DomainException(this)
    }
}