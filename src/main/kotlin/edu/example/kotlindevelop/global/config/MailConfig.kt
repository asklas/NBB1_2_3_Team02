package edu.example.kotlindevelop.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class MailConfig {
    @Value("\${spring.mail.host}")
    private val mailHost: String? = null

    @Value("\${spring.mail.port}")
    private val mailPort = 0

    @Value("\${spring.mail.username}")
    private val mailUsername: String? = null

    @Value("\${spring.mail.password}")
    private val mailPassword: String? = null

    @Bean
    fun javaMailSender(): JavaMailSender {
        val javaMailSender = JavaMailSenderImpl()

        javaMailSender.host = mailHost
        javaMailSender.port = mailPort
        javaMailSender.username = mailUsername
        javaMailSender.password = mailPassword

        javaMailSender.javaMailProperties = mailProperties

        return javaMailSender
    }

    private val mailProperties: Properties
        get() {
            val properties = Properties()
            properties.setProperty("mail.transport.protocol", "smtp")
            properties.setProperty("mail.smtp.auth", "true")
            properties.setProperty("mail.smtp.starttls.enable", "true")
            properties.setProperty("mail.debug", "true")
            properties.setProperty("mail.smtp.ssl.trust", mailHost)

            return properties
        }
}
