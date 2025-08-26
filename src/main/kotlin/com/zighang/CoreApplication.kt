package com.zighang

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CoreApplication

fun main(args: Array<String>) {
    runApplication<CoreApplication>(*args)
}

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!