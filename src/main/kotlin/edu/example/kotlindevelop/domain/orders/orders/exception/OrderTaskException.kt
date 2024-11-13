package edu.example.kotlindevelop.domain.orders.orders.exception

class OrderTaskException(
    override val message: String? = null,
    val code: Int = 0
) : RuntimeException(message)
