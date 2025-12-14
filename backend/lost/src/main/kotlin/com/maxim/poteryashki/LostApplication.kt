package com.maxim.poteryashki

import com.maxim.poteryashki.lost.adapter.s3.MinioProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties::class)
class LostApplication

fun main(args: Array<String>) {
    runApplication<LostApplication>(*args)
}