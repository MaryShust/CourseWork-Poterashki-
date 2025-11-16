package com.maxim.poteryashki.gateway.config

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


@Configuration
class JwtConfig {

    @Bean
    fun jwtEncoder(@Value("\${security.jwt.secret}") secret: String): JwtEncoder {
        val key = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        val jwk = OctetSequenceKey.Builder(key).algorithm(JWSAlgorithm.HS256).build()
        val jwks = JWKSet(jwk)
        val jwkSource: JWKSource<SecurityContext?> =
            JWKSource { selector: JWKSelector?, ctx: SecurityContext? -> selector!!.select(jwks) }
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun jwtDecoder(@Value("\${security.jwt.secret}") secret: String): JwtDecoder {
        val key = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build()
    }
}