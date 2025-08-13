package com.zighang.core.exception.error

import org.springframework.http.HttpStatus

interface BaseErrorCode<T: Exception> {

    fun name(): String;

    fun getMessage() : String;

    fun getHttpStatus(): HttpStatus;

    fun toException(): T;
}