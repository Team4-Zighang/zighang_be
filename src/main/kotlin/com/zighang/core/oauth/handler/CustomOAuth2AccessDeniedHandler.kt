package com.zighang.core.oauth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zighang.core.oauth.exception.OAuth2ErrorCode
import com.zighang.core.presentation.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
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

        val httpStatusForResponse = OAuth2ErrorCode.OAUTH2_UNAUTHORIZED_ERROR

        val errorResponse = ErrorResponse(
            statusCode = httpStatusForResponse.httpStatus.value(),
            code = httpStatusForResponse.httpStatus.name,
            message = httpStatusForResponse.message,
        )

        response.status = httpStatusForResponse.httpStatus.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()

        val responseJson = objectMapper.writeValueAsString(errorResponse)
        response.writer.write(responseJson)

        logger.error("Access Denined : ${accessDeniedException.message}")
    }
}