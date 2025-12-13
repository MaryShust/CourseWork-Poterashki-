package com.maxim.poteryashki.lost.s3

import io.minio.MinioClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@ConfigurationProperties(prefix = "minio")
data class MinioProperties(

    val endpoint: String,

    val accessKey: String,

    val secretKey: String,
)

@Configuration
class MinioConfiguration {

    @Bean
    fun minioClient(minioProperties: MinioProperties) = MinioClient.builder()
        .endpoint(minioProperties.endpoint)
        .credentials(minioProperties.accessKey, minioProperties.secretKey)
        .build()

}