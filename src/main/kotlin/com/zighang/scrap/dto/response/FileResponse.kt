package com.zighang.scrap.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class FileResponse(
    @Schema(description = "파일 url", example = "https://s3:resume-233333")
    val fileUrl : String?,
    @Schema(description = "파일 원본 이름", example = "OOO파일")
    val originalFileName : String?,
) {
    companion object {
        fun create(fileUrl: String?, originalFileName: String?) : FileResponse{
            return FileResponse(fileUrl, originalFileName)
        }
    }
}
