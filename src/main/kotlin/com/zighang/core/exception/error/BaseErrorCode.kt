package com.zighang.core.exception.error

import org.springframework.http.HttpStatus

interface BaseErrorCode<T: Exception> {

    val name: String;

    val message: String;

    val httpStatus: HttpStatus;

    fun toException(): T;
}