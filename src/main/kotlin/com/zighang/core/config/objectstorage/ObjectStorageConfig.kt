package com.zighang.core.config.objectstorage

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectStorageConfig(

    @Value("\${ncp.object-storage.endpoint}")
    private val endpoint: String,

    @Value("\${ncp.object-storage.region}")
    private val region: String,

    @Value("\${ncp.object-storage.access-key}")
    private val accessKey: String,

    @Value("\${ncp.object-storage.secret-key}")
    private val secretKey: String
) {

    @Bean
    fun amazonS3Client(): AmazonS3Client {
        var awsCredentials : AWSCredentials = BasicAWSCredentials(accessKey, secretKey)

        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .withPathStyleAccessEnabled(true)
            .build() as AmazonS3Client
    }
}