package com.zighang.core.config.swagger

import com.zighang.core.exception.error.BaseErrorCode
import com.zighang.core.presentation.ErrorResponse
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import kotlin.reflect.KClass

@Configuration
class SwaggerConfig(
    @Value("\${springdoc.server-url}")
    private val baseUrl: String,
) {

    @Bean
    fun openAPI(): OpenAPI {
        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")

        val securityRequirement = SecurityRequirement().addList("bearerAuth")

        val server = Server()
            .url(baseUrl)
            .description("Production Server")

        return OpenAPI()
            .info(getInfo())
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
            .servers(listOf(server))
            .addSecurityItem(securityRequirement)
    }

    private fun getInfo(): Info {

        return Info()
            .title("Team4_4호선직행")
            .version("1.0.0")
            .description("KUSITMS X 직행 기업프로젝트 Team4_4호선직행 - API Docs")
    }


    @Bean
    fun operationCustomizer(): OperationCustomizer {
        return OperationCustomizer { operation: Operation, handlerMethod: HandlerMethod ->
            val apiErrorCode = handlerMethod.getMethodAnnotation(ApiErrorCode::class.java)
            if(apiErrorCode != null){
                generateErrorCodeResponseExample(operation, apiErrorCode.value)
            }
            operation
        }
    }

    private fun generateErrorCodeResponseExample(operation: Operation,  types: Array<out KClass<out BaseErrorCode<*>>>) {
        val response = operation.responses
        val exampleHolders = mutableListOf<ExampleHolder>()

        types.forEach { type ->
            val errorCodes = type.java.enumConstants
            errorCodes.forEach { baseErrorCode  ->
                val holder = ExampleHolder(
                    holder = getSwaggerExample(baseErrorCode),
                    code = baseErrorCode.httpStatus.value(),
                    name = baseErrorCode.name
                )
                exampleHolders.add(holder)
            }
        }

        val statusWithExampleHolders = exampleHolders.groupBy { it.code }

        addExamplesToResponses(response, statusWithExampleHolders)
    }

    private fun getSwaggerExample(baseErrorCode: BaseErrorCode<*>): Example {

        val errorResponse = ErrorResponse.createSwaggerResponse(
            baseErrorCode.httpStatus.value(),
            baseErrorCode.toException()
        )

        return Example().apply {
            value = errorResponse
        }

    }

    private fun addExamplesToResponses(responses: ApiResponses, statusWithExampleHolders: Map<Int, List<ExampleHolder>>) {
        statusWithExampleHolders.forEach { (status, value) ->
            val content = Content()
            val mediaType = MediaType()
            val apiResponse = ApiResponse()
            value.forEach { exampleHolder ->
                mediaType.addExamples(exampleHolder.name, exampleHolder.holder)
            }
            content.addMediaType("application/json", mediaType)
            apiResponse.content = content
            responses.addApiResponse(status.toString(), apiResponse)
        }
    }
}