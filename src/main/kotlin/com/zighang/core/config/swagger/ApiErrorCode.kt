package com.zighang.core.config.swagger

import com.zighang.core.exception.error.BaseErrorCode
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrorCode(
    val value: Array<out KClass<out BaseErrorCode<*>>>
)
