package com.maxim.poteryashki.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.mappings")
data class ServiceMappingConfig(
    val list: List<Mapping>
)

data class Mapping(
    val prefix: String,
    val host: String
)