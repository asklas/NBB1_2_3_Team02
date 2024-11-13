package edu.example.kotlindevelop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
class KotlindevelopApplication

fun main(args: Array<String>) {
	runApplication<KotlindevelopApplication>(*args)
}
