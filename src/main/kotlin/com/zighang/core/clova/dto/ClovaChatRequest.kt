package com.zighang.core.clova.dto

import com.dodream.job.dto.request.clova.ClovaMessage

data class ClovaChatRequest(
    val messages: List<ClovaMessage>,

    val maxTokens: Int,
)