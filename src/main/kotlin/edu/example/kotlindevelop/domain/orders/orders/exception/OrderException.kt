package edu.example.kotlindevelop.domain.orders.orders.exception

enum class OrderException(message: String, code: Int) {
    NOT_FOUND("Order NOT_FOUND", 400),
    NOT_REGISTERED("Order NOT_REGISTERED", 400),
    NOT_REMOVED("Order NOT_REMOVED", 400),
    NOT_FETCHED("Order NOT_FETCHED", 400),
    REGISTER_ERR("NO AUTHENTICATED_USER", 403);

    private val orderTaskException: OrderTaskException

    init {
        orderTaskException = OrderTaskException(message, code)
    }

    fun get(): OrderTaskException {
        return orderTaskException
    }
}