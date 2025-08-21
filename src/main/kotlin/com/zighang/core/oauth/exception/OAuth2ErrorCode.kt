package com.zighang.core.oauth.exception

import com.zighang.core.exception.DomainException
import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

enum class OAuth2ErrorCode (
    override val httpStatus: HttpStatus,
    override val message: String,
): BaseErrorCode<DomainException> {

    OAUTH2_PROVIDER_ERROR(HttpStatus.BAD_REQUEST, "oAuth2 제공자 이름이 잘못되었습니다."),
    IS_NOT_OAUTH2_USER(HttpStatus.INTERNAL_SERVER_ERROR, "oAuth2 사용자가 아닙니다."),
    CANNOT_FIND_EMAIL_IN_KAKAO(HttpStatus.NOT_FOUND, "카카오 이메일 정보를 찾을 수 없습니다."),
    CANNOT_FIND_NICKNAME_IN_KAKAO(HttpStatus.NOT_FOUND, "카카오 닉네임 정보를 찾을 수 없습니다."),
    OAUTH2_UNAUTHORIZED_ERROR(HttpStatus.FORBIDDEN, "해당 자원에 접근할 권한이 없습니다.");

    override fun toException(): DomainException {
        return DomainException(this)
    }
}