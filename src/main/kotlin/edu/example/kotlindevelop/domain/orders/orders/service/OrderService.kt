package edu.example.kotlindevelop.domain.orders.orders.service

import edu.example.kotlindevelop.domain.member.exception.MemberException
import edu.example.kotlindevelop.domain.member.repository.MemberRepository
import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import edu.example.kotlindevelop.domain.orders.orderItem.repository.OrderItemRepository
import edu.example.kotlindevelop.domain.orders.orders.dto.OrderDTO
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.orders.orders.exception.OrderException
import edu.example.kotlindevelop.domain.orders.orders.repository.OrderRepository
import edu.example.kotlindevelop.domain.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val orderItemRepository: OrderItemRepository
) {

    @Transactional
    fun createOrder(orderDTO: OrderDTO, memberId: Long): Orders {
        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.memberTaskException }

        val orders = orderDTO.toEntity(member, productRepository) // DTO에서 엔티티로 변환
        return orderRepository.save(orders)
    }

    fun read(orderId: Long?): OrderDTO? {
        val orders =
            orderRepository.findById(orderId!!)
                .orElseThrow { OrderException.NOT_FOUND.get() }


        return orders?.let { OrderDTO(it) }
    }

    @Transactional
    fun delete(orderId: Long?) {
        val orders = orderId?.let {
            orderRepository.findById(it)
                .orElseThrow { OrderException.NOT_FOUND.get() }
        }
        try {
            if (orders != null) {
                orderRepository.delete(orders)
            }
        } catch (e: Exception) {
            throw OrderException.NOT_REMOVED.get()
        }
    }

    fun getList(pageRequestDTO: OrderDTO.PageRequestDTO, memberId: Long): Page<OrderDTO.OrderListDTO> {
        val sort = Sort.by("id").descending()
        val pageable: Pageable = pageRequestDTO.getPageable(sort)

        val member = memberRepository.findById(memberId)
            .orElseThrow { MemberException.MEMBER_NOT_FOUND.memberTaskException }
        val ordersPage = orderRepository.findByMember(member, pageable)
        return ordersPage!!.map { OrderDTO.OrderListDTO(it) }
    }

    fun getMonthlyOrderSummary(memberId: Long): List<Map<String, Any?>> {
        val member = memberRepository.findById(memberId)
            .orElseThrow { RuntimeException("Member not found") }

        val results = orderRepository.getMonthlyTotalPrice(member)

        // 결과를 Map 형태로 변환
        return results?.map { result ->
            mapOf(
                "orderMonth" to result!![0], // 월
                "totalPrice" to result[1]  // 총 금액
            ) ?: emptyMap() // null일 경우 빈 맵 반환
        } ?: emptyList() // results가 null일 경우 빈 리스트 반환
    }

    fun getMonthlyAveragePrices(): Map<String, Map<String, Int>> {
        val orderItems = orderItemRepository.findAll()

        // 월별 및 상품별 평균 단가 계산
        return orderItems.groupBy { it?.orders?.createdAt?.month?.name ?: "Unknown" } // 월 이름
            .mapValues { (_, items) ->
                items.filterNotNull().groupBy { it.product?.name ?: "chicken" } // 상품 이름
                    .mapValues { (_: String, productItems: List<OrderItem>) -> // 타입 명시
                        productItems.sumOf { it.price * it.quantity } / productItems.sumOf { it.quantity } // 평균 단가 계산
                    }
            }
    }


    fun getList(month: Int?, pageRequestDTO: OrderDTO.PageRequestDTO, memberId: Long?): Page<OrderDTO.OrderListDTO> {
        val sort = Sort.by(Sort.Order.desc("id"))
        val pageable: Pageable = pageRequestDTO.getPageable2(sort)

        val member = memberId?.let {
            memberRepository.findById(it)
                .orElseThrow { MemberException.MEMBER_NOT_FOUND.memberTaskException }
        }

        if (month !in 1..12) {
            throw IllegalArgumentException("Invalid month. Please provide a valid month as a number between 1 and 12.")
        }

        // 선택한 month와 member로 주문 조회, 주문 횟수에 따라 정렬된 결과 반환
        val ordersPage = orderRepository.findOrdersByMemberAndMonthWithItemCountDesc(member, month!!, pageable)
        return ordersPage.map { OrderDTO.OrderListDTO(it) }
    }

    fun getPrevMonthOrders(memberId: Long): List<OrderDTO.OrderListDTO> {
        // 이전 월의 시작과 끝 날짜 계산
        val now = LocalDate.now()
        val startOfLastMonth = now.minusMonths(1).withDayOfMonth(1)
        val endOfLastMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth())

// LocalDateTime으로 변환
        val startOfLastMonthDateTime: LocalDateTime = startOfLastMonth.atStartOfDay()
        val endOfLastMonthDateTime: LocalDateTime = endOfLastMonth.atTime(23, 59, 59)

// 해당 기간의 주문 내역 조회
        val orders = orderRepository.findByMemberIdAndCreatedAtBetween(
            memberId,
            startOfLastMonthDateTime,
            endOfLastMonthDateTime
        )

        val response = mutableListOf<OrderDTO.OrderListDTO>()
        for (order in orders) {
            response.add(OrderDTO.OrderListDTO(order))
        }

        return response

    }
}
