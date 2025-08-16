package com.zighang.core.config.swagger

import io.swagger.v3.oas.models.examples.Example

data class ExampleHolder(
    val holder: Example,

    val code: Int,

    val name: String,
)