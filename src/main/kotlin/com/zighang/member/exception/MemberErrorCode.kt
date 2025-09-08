package com.zighang.member.exception

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

enum class MemberErrorCode (
    override val httpStatus: HttpStatus,
    override val message: String,
) : BaseErrorCode<DomainException> {

    NOT_EXIST_MEMBER(HttpStatus.NOT_FOUND, "해당 멤버가 존재하지 않습니다.");

    override fun toException(): DomainException {
        return DomainException(this)
    }
}