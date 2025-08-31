package com.zighang.card.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.*
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Company(
    val companyName: String? = null,
    val companyImageUrl: String? = null
)
