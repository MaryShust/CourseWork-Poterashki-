package com.maxim.poteryashki.lost.adapter.s3

import io.minio.BucketExistsArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.SetBucketPolicyArgs
import io.minio.errors.MinioException
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val logger = LoggerFactory.getLogger(S3Adapter::class.java)

@Component
class S3Adapter(
    private val minioClient: MinioClient,

    @Value("#{app.s3.photo-bucket}")
    private val minioBucket: String,
) {

    @PostConstruct
    fun init() {
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucket).build())) {
                logger.info("Bucket '$minioBucket' already exists.")
                return
            }

            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(minioBucket)
                    .build()
            )

            val policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": "*",
                      "Action": [
                        "s3:GetObject"
                      ],
                      "Resource": [
                        "arn:aws:s3:::$minioBucket/*"
                      ]
                    }
                  ]
                }
            """.trimIndent()

            minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                    .bucket(minioBucket)
                    .config(policy)
                    .build()
            )

        } catch (e: MinioException) {
            logger.info("MinIO error: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            println("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }

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