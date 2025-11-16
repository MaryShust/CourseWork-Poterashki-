package com.maxim.poteryashki.gateway.config

import com.maxim.poteryashki.gateway.service.security.AuthGatewayFilter
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.RouterFunctions
import org.springframework.web.servlet.function.ServerResponse


@Configuration
class RoutingConfig(
    private val authGatewayFilter: AuthGatewayFilter,
    private val serviceMappingConfig: ServiceMappingConfig
) {

    @Bean
    fun lostServiceRouting() =
        routeTemplate(serviceMappingConfig.list.find { it.service == "lost" }!!)


private fun routeTemplate(mapping: Mapping) =
    routeTemplate(mapping.service, mapping.prefix, mapping.host)

    private fun routeTemplate(serviceName: String, prefix: String, host: String): RouterFunction<ServerResponse?> {
        return route("${serviceName}_route")
            .route(path(prefix), http())
            .before(uri(host))
            .filter(authGatewayFilter)
            .build()
    }




}