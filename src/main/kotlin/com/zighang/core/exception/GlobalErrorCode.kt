package com.zighang.core.exception

import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

enum class GlobalErrorCode(
    override val httpStatus: HttpStatus,
    override val message: String
) : BaseErrorCode<DomainException> {

    // 예시 에러 코드
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "유효한 리프레쉬 토큰이 아닙니다."),
    NOT_EXIST_MEMBER(HttpStatus.BAD_REQUEST, "해당 멤버가 존재하지 않습니다."),
    NOT_EXIST_ONBOARDING(HttpStatus.BAD_REQUEST, "온보딩 데이터가 존재하지 않습니다"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    CLOVA_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "클로바 API 연결에 실패했습니다.");


    override fun toException(): DomainException {
        return DomainException(this)
    }
}