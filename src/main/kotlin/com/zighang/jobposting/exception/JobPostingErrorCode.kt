package com.zighang.jobposting.exception

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

enum class JobPostingErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String,
) : BaseErrorCode<DomainException> {

    NOT_EXISTS_JOB_POSTING(HttpStatus.NOT_FOUND, "해당 공고를 찾을 수 없습니다.");

    override fun toException(): DomainException {
        return DomainException(this)
    }
}