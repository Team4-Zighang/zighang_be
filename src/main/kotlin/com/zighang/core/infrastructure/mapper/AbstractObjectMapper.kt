package com.zighang.core.infrastructure.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.exception.GlobalErrorCode


abstract class AbstractObjectMapper<T>(protected val objectMapper: ObjectMapper) {
    fun <R> parse(json: String?, clazz: Class<R>?): R {
        try {
            return objectMapper.readValue(json, clazz)
        } catch (e: Exception) {
            throw GlobalErrorCode.NOT_CONVERT_JSON_TO_OBJECT.toException()
        }
    }
}