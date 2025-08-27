package com.zighang.core.clova.infrastructure

import com.zighang.core.clova.config.ClovaFeignClientConfig
import com.zighang.core.clova.dto.ClovaStudioRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "clovaFeignClient",
    url = "\${ncp.clova.url}",
    configuration = [ClovaFeignClientConfig::class]
)
interface ClovaFeignClient {
    @PostMapping("/testapp/v3/chat-completions/HCX-DASH-002")
    fun callClovaStudio(@RequestBody clovaChatRequest: ClovaStudioRequest): String
}