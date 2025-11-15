package com.maxim.poteryashki.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer.JwtConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity, jwtAuthConverter: JwtAuthenticationConverter?): SecurityFilterChain? {
        http
            .csrf { csrf: CsrfConfigurer<HttpSecurity?>? -> csrf!!.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/actuator/health", "/public/**", "/auth/token").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement(Customizer { sm: SessionManagementConfigurer<HttpSecurity?>? ->
                sm!!.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            })
            .oauth2ResourceServer { oauth: OAuth2ResourceServerConfigurer<HttpSecurity?>? ->
                oauth!!.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter) }
            }
        return http.build()
    }


    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val gac = JwtGrantedAuthoritiesConverter()
        gac.setAuthoritiesClaimName("authorities")
        gac.setAuthorityPrefix("") // уже будут вида ROLE_*
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(gac)
        return converter
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // Пример: in-memory пользователи. Замените на свой UserDetailsService/репозиторий
    @Bean
    fun userDetailsService(encoder: PasswordEncoder): UserDetailsService {
        val user: UserDetails = User.withUsername("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build()
        val admin: UserDetails? = User.withUsername("admin")
            .password(encoder.encode("password"))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(user, admin)
    }

    // AuthenticationManager для аутентификации по username/password в /auth/token
    @Bean
    fun authenticationManager(uds: UserDetailsService?, encoder: PasswordEncoder?): AuthenticationManager {
        val provider = DaoAuthenticationProvider(
            encoder
        )
        provider.setUserDetailsService(uds)
        return ProviderManager(provider)
    }
}