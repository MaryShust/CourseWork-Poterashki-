package com.maxim.poteryashki.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.mappings")
data class ServiceMappingConfig(
    val list: List<Mapping>
)

/**
 * ВАЖНО: все префиксы должны начинаться с /
 * И не должны содержать друг друга
 */
data class Mapping(
    val service: String,
    val prefix: String,
    val host: String
)