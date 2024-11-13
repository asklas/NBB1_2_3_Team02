package edu.example.kotlindevelop.domain.member.util

import java.security.SecureRandom

object PasswordUtil {
    private const val CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"

    fun generateTempPassword(): String {
        val random = SecureRandom()
        val tempPassword = StringBuilder(6)
        for (i in 0..5) {
            val index = random.nextInt(CHAR_SET.length)
            tempPassword.append(CHAR_SET[index])
        }
        return tempPassword.toString()
    }
}