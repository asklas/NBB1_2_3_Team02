package edu.example.kotlindevelop.domain.orders.orders.dto

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.orders.orderItem.dto.OrderItemDTO
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.product.entity.Product
import edu.example.kotlindevelop.domain.product.repository.ProductRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// "주문 데이터 전송 객체"


data class OrderDTO(
    val id: Long,
    var items: List<OrderItemDTO> = emptyList(),
    val totalPrice: Long

) {
    // 기존 엔티티로 변환하는 메서드

    fun toEntity(member: Member, productRepository: ProductRepository): Orders {
        val orders = Orders(0L, member)
        require(items.isNotEmpty()) { "Order items cannot be null or empty" }

        items.forEach { itemDTO ->
            val product: Product? = itemDTO.productId?.let {
                productRepository.findById(it)
                    .orElseThrow { RuntimeException("Product not found") }
            }
            orders.addOrderItem(product, itemDTO.quantity, itemDTO.price)
        }
        return orders
    }

    // 엔티티로부터 DTO 생성
    constructor(orders: Orders) : this(
        id = orders.id ?: 0L,
        items = orders.orderItems.map { OrderItemDTO(it) }, // OrderItemDTO 변환
        totalPrice = orders.totalPrice
    )

    // 내부 클래스 - 주문 목록을 위한 DTO
    data class OrderListDTO(
        var id: Long? = null, // 주문 ID
        var memberId: Long? = null, // 회원 ID
        var totalPrice: Long? = null, // 총 가격
        var createdAt: String? = null, // 생성일
        var orderItems: List<OrderItemDTO>? = null // 주문 항목 리스트
    ) {
        // 엔티티로부터 DTO 생성
        constructor(orders: Orders?) : this(
            id = orders!!.id,
            memberId = orders.member.id,
            totalPrice = orders.totalPrice,
            createdAt = orders.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), // 포맷 적용
            orderItems = orders.orderItems.map { OrderItemDTO(it) } // OrderItem을 OrderItemDTO로 변환
        )
    }
    data class PrevOrderListDTO(
        var id: Long? = null, // 주문 ID
        var memberId: Long? = null, // 회원 ID
        var totalPrice: Long? = null, // 총 가격
        var createdAt: String? = null, // 생성일
        var orderItems: List<OrderItemDTO>? = null // 주문 항목 리스트
    ) {
        // 엔티티로부터 DTO 생성
        constructor(orders: Orders?) : this(
            id = orders!!.id,
            memberId = orders.member.id,
            totalPrice = orders.totalPrice,
            createdAt = orders.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), // 포맷 적용
            orderItems = orders.orderItems.map { OrderItemDTO(it) } // OrderItem을 OrderItemDTO로 변환
        )
    }

    // 내부 클래스 - 페이지 요청 DTO
    data class PageRequestDTO(
        val page: Int = 1,
        val size: Int = 10
    ) {
        fun getPageable(sort: Sort?): Pageable {
            val pageNum = if (page < 0) 1 else page - 1
            val sizeNum = if (size <= 10) 10 else size

            return PageRequest.of(pageNum, sizeNum)
        }

        fun getPageable2(sort: Sort): Pageable {
            val pageNum = if (page < 0) 1 else page - 1
            val sizeNum = if (size <= 10) 10 else size

            return PageRequest.of(pageNum, sizeNum, sort)
        }
    }
}
