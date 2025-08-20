package com.zighang.core.oauth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import com.zighang.core.presentation.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class CustomOAuth2AccessDeniedHandler(
    private val objectMapper : ObjectMapper
) : AccessDeniedHandler {

    private val logger = LoggerFactory.getLogger(CustomOAuth2AccessDeniedHandler::class.java)

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {

        if(response.isCommitted) return;


        val httpStatusForResponse = OAuth2ErrorCode.OAUTH2_UNAUTHORIZED_ERROR.httpStatus
        val customErrorCodeName = "ACCESS_DENIED_ERROR"
        val customErrorMessage = "해당 자원에 접근할 권한이 없습니다."

        val errorResponse = ErrorResponse(
            statusCode = HttpStatus.FORBIDDEN.value(),
            code = customErrorCodeName,
            message = customErrorMessage,
        )
        response.status = httpStatusForResponse.value() // 403 Forbidden
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()

        val responseJson = objectMapper.writeValueAsString(errorResponse)
        response.writer.write(responseJson)

        logger.error("Access Denined : ${accessDeniedException.message}")
    }
}