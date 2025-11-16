package com.maxim.poteryashki.gateway.config

import com.maxim.poteryashki.gateway.db.user.UserDao
import com.maxim.poteryashki.gateway.service.security.DatabaseUserDetailService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
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
            .sessionManagement{ sm: SessionManagementConfigurer<HttpSecurity?>? ->
                sm!!.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .oauth2ResourceServer { oauth: OAuth2ResourceServerConfigurer<HttpSecurity?>? ->
                oauth!!.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter) }
            }
        return http.build()
    }


    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val gac = JwtGrantedAuthoritiesConverter()
            .apply { setAuthoritiesClaimName("authorities") }
           .apply { setAuthorityPrefix("") }
        return JwtAuthenticationConverter()
            .apply { setJwtGrantedAuthoritiesConverter(gac) }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(userDao: UserDao): UserDetailsService =
        DatabaseUserDetailService(userDao)


    @Bean
    fun authenticationManager(uds: UserDetailsService?, encoder: PasswordEncoder?): AuthenticationManager {
        val provider = DaoAuthenticationProvider(
            encoder
        )
        provider.setUserDetailsService(uds)
        return ProviderManager(provider)
    }
}