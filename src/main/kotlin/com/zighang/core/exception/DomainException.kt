package com.zighang.core.exception

import com.zighang.core.exception.error.BaseErrorCode
import org.springframework.http.HttpStatus

class DomainException(
    override val message: String,
): RuntimeException(message){

    var httpStatus: HttpStatus? = null
        private set
    var code: String? = null
        private set

    constructor(message: String, httpStatus: HttpStatus): this(message) {
        this.httpStatus = httpStatus
    }

    constructor(baseErrorCode: BaseErrorCode<*>): this(baseErrorCode.name()){
        this.httpStatus = baseErrorCode.getHttpStatus()
        this.code = baseErrorCode.name()
    }
}