package com.maxim.poteryashki.lost.s3

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class S3Adapter(
    private val minioClient: MinioClient,

    @Value("#{app.s3.photo-bucket}")
    private val minioBucket: String,
) {

    /**
     * Загрузка фото в s3
     * @return ссылка на фото
     */
    fun uploadPhoto(photo: ByteArray, fileName: String): String {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(minioBucket)
                .`object`(fileName)
                .stream(
                    photo.inputStream(),
                    photo.size.toLong(),
                    -1
                )
                .build()
        )

        val response = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(minioBucket)
                .`object`(fileName)
                .build()
        )

        return response
    }
}