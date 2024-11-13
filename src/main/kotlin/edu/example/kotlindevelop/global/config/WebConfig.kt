package edu.example.kotlindevelop.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "http://aws-est-env.eba-qadyyncj.ap-northeast-2.elasticbeanstalk.com") // React 앱 URL
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    }
}