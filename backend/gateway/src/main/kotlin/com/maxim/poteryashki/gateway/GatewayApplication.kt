package com.maxim.poteryashki.gateway

import com.maxim.poteryashki.gateway.config.ServiceMappingConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(ServiceMappingConfig::class)
@SpringBootApplication
class GatewayApplication

fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}
