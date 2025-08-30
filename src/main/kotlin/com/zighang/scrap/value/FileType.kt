package com.zighang.scrap.value

import com.zighang.core.application.ObjectStorageService
import com.zighang.scrap.dto.response.FileDeleteResponse
import com.zighang.scrap.entity.Scrap
import org.springframework.web.multipart.MultipartFile

enum class FileType(
    val urlGetter: (Scrap) -> String?,
    val urlSetter: (Scrap, String?) -> Unit,
    val responseFactory: (Boolean, String) -> FileDeleteResponse,
    val uploadFunction: (ObjectStorageService, MultipartFile, Long) -> String
) {
    RESUME(
        { it.resumeUrl },
        { scrap, _ -> scrap.resumeUrl = null },
        { success, filename -> FileDeleteResponse.resumeDeleteCreate(success, filename) },
        { storage, file, scrapId -> storage.uploadResumeFile(file, scrapId) }
    ),
    PORTFOLIO(
        { it.portfolioUrl },
        { scrap, _ -> scrap.portfolioUrl = null },
        { success, filename -> FileDeleteResponse.portfolioDeleteCreate(success, filename) },
        { storage, file, scrapId -> storage.uploadPortpolioFile(file, scrapId) }
    )
}