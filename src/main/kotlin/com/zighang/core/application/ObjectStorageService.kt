package com.zighang.core.application

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.zighang.core.exception.GlobalErrorCode
import io.jsonwebtoken.io.IOException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Service
class ObjectStorageService(

    private val amazonS3Client: AmazonS3Client,

    @Value("\${ncp.object-storage.bucket-name}")
    private val bucketName: String,
) {

    fun uploadResumeFile(
        file: MultipartFile,
        scrapId: Long,
    ) : String {
        val dirName = "resume/$scrapId"
        val fileName = dirName + UUID.randomUUID().toString() + "_" + file.originalFilename

        return uploadFile(fileName, file)
    }

    fun uploadPortpolioFile(
        file: MultipartFile,
        scrapId: Long,
    ) : String {
        val dirName = "portpolio/$scrapId"
        val fileName = dirName + UUID.randomUUID().toString() + "_" + file.originalFilename

        return uploadFile(fileName, file)
    }

    fun deleteFile(imageUrl: String) {
        val fileKey: String = extractFileKeyFromUrl(imageUrl)

        try {
            if (amazonS3Client.doesObjectExist(bucketName, fileKey)) {
                amazonS3Client.deleteObject(bucketName, fileKey)
            }
        } catch (e: SdkClientException) {
            throw GlobalErrorCode.FILE_DELETE_FAILED.toException()
        }
    }

    private fun uploadFile(
        fileName: String, file: MultipartFile
    ) : String {
        var metadata: ObjectMetadata = ObjectMetadata()
        metadata.contentLength = file.size
        metadata.contentType = file.contentType

        try {
            var putRequest: PutObjectRequest? = PutObjectRequest(
                bucketName, fileName, file.inputStream, metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead)

            amazonS3Client.putObject(putRequest)
        } catch(e: IOException){
            throw GlobalErrorCode.FILE_UPLOAD_FAILED.toException()
        }

        return amazonS3Client.getResourceUrl(bucketName, fileName).toString()
    }

    private fun isAllowedImageExtension(filename: String): Boolean {

        val lowerName = filename.lowercase(Locale.getDefault())

        return lowerName.endsWith(".jpg") ||
                lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") ||
                lowerName.endsWith(".pdf")
    }

    private fun extractFileKeyFromUrl(url: String): String {
        val idx = url.indexOf(".com/")
        require(idx != -1) { throw IllegalArgumentException("잘못된 URL 형식입니다: $url") }
        return url.substring(idx + 5)
    }
}