package com.zighang.core.exception

import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

class DomainException(
    override val message: String,
): RuntimeException(message){

    private var httpStatus: HttpStatus? = null
    private var code: String? = null

    constructor(message: String, httpStatus: HttpStatus): this(message) {
        this.httpStatus = httpStatus
    }

    constructor(message: String, baseErrorCode: BaseErrorCode<*>): this(baseErrorCode.name()){
        this.httpStatus = baseErrorCode.getHttpStatus()
        this.code = baseErrorCode.name()
    }
}