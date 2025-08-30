package com.zighang.scrap.dto.response

data class FileDeleteResponse(

    val status: Boolean,

    val message: String
) {

    companion object{
        fun resumeDeleteCreate(status: Boolean, fileName: String) : FileDeleteResponse {
            return FileDeleteResponse(
                status,
                "이력서 파일 : $fileName 삭제에 성공했습니다."
            )
        }

        fun portfolioDeleteCreate(status: Boolean, fileName: String) : FileDeleteResponse {
            return FileDeleteResponse(
                status,
                "포트폴리오 파일 : $fileName 삭제에 성공했습니다."
            )
        }
    }
}