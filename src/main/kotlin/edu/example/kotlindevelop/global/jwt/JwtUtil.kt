package edu.example.kotlindevelop.global.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${spring.custom.jwt.secretKey}")
    lateinit var secretKeyString: String

    private lateinit var secretKey: SecretKey

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray(StandardCharsets.UTF_8))
    }
    fun encodeAccessToken(minute: Long, data: Map<String, Any>): String {
        val claims = Jwts
            .claims()
            .add("data",data)
            .add("type","access_token")
            .build()

        val now = Date()
        val expiration = Date(now.time + 1000 * 60 * minute)

        return Jwts.builder()
            .subject("NBE2_T2")
            .claims(claims)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun encodeRefreshToken(minute: Long, data: Map<String, Any>): String {
        val claims = Jwts
            .claims()
            .add("data",data)
            .add("type","refresh_token")
            .build()

        val now = Date()
        val expiration = Date(now.time + 1000 * 60 * minute)

        return Jwts.builder()
            .subject("NBE2_T2")
            .claims(claims)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun decode(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}