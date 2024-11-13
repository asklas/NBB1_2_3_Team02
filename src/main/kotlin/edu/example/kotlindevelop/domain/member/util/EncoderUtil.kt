package edu.example.kotlindevelop.domain.member.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

object EncoderUtil {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}