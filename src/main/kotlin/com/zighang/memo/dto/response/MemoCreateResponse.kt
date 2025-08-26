package com.zighang.memo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class MemoCreateResponse(

    @Schema(description = "메모 식별자", example = "1")
    val memoId: Long,

    @Schema(description = "메시지", example = "메모 저장이 완료되었습니다.")
    val message: String,
){
    companion object {
        fun create(id: Long, message: String): MemoCreateResponse {
            return MemoCreateResponse(
                id, message
            )
        }
    }
}