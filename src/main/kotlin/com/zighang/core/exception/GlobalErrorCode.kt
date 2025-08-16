package com.zighang.core.exception

import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

enum class GlobalErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String
) : BaseErrorCode<RuntimeException> {

    // 예시 에러 코드
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다.");

    override fun toException(): RuntimeException {
        return RuntimeException(message)
    }
}