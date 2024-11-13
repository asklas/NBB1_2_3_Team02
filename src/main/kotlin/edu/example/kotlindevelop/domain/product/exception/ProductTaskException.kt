package edu.example.kotlindevelop.domain.product.exception

import org.springframework.http.HttpStatus

class ProductTaskException(
    override val message: String,
    val status: HttpStatus
) : RuntimeException(message)