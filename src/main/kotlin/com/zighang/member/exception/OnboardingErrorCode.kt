package com.zighang.member.exception

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

enum class OnboardingErrorCode (
    override val httpStatus: HttpStatus,
    override val message: String,
) : BaseErrorCode<DomainException> {

    NOT_EXISTS_ONBOARDING(HttpStatus.NOT_FOUND, "온보딩 정보가 존재하지 않습니다.")

    ;

    override fun toException(): DomainException {
        return DomainException(this)
    }
}