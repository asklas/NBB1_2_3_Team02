package edu.example.kotlindevelop.domain.orders.orderItem.dto

import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.product.entity.Product


data class OrderItemDTO(
    var productId: Long? = 0,  // 상품 ID
    var quantity: Int = 0,        // 수량
    var price: Int = 0            // 사용자가 입력한 가격
) {
    // DTO -> 엔티티 변환 메서드
    fun toEntity(product: Product, orders: Orders): OrderItem {
        return OrderItem(
            product = product,
            orders = orders,
            quantity = this.quantity,
            price = this.price
        )
    }

    // 엔티티로부터 DTO 생성
    constructor(orderItem: OrderItem) : this(
        productId = orderItem.product?.id,
        quantity = orderItem.quantity,
        price = orderItem.price
    )
}