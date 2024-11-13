//package edu.example.kotlindevelop.domain.orders.orders.entity
//
//import edu.example.kotlindevelop.domain.member.entity.Member
//import edu.example.kotlindevelop.domain.member.repository.MemberRepository
//import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
//import edu.example.kotlindevelop.domain.orders.orderItem.repository.OrderItemRepository
//import edu.example.kotlindevelop.domain.orders.orders.dto.OrderDTO
//import edu.example.kotlindevelop.domain.orders.orders.exception.OrderException
//import edu.example.kotlindevelop.domain.orders.orders.repository.OrderRepository
//import edu.example.kotlindevelop.domain.orders.orders.service.OrderService
//import edu.example.kotlindevelop.domain.product.ProductRepository
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito
//import org.mockito.kotlin.whenever
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.data.domain.PageImpl
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Pageable
//import org.springframework.data.domain.Sort
//
//@SpringBootTest
//class OrderServiceTest {
//
//    private val orderRepository: OrderRepository = Mockito.mock(OrderRepository::class.java)
//    private val productRepository: ProductRepository = Mockito.mock(ProductRepository::class.java)
//    private val memberRepository: MemberRepository = Mockito.mock(MemberRepository::class.java)
//    private val orderItemRepository: OrderItemRepository = Mockito.mock(OrderItemRepository::class.java)
//
//    private val orderService = OrderService(
//        orderRepository,
//        productRepository,
//        memberRepository,
//        orderItemRepository
//    )
//
//    @Test
//    fun `createOrder should save order successfully`() {
//        // Given
//        val member = Member(loginId = "testUser", email = "test@example.com", pw = "password", name = "Test User")
//        val orderDTO = OrderDTO(/* 필요한 필드 설정 */)
//
//        whenever(memberRepository.findById(member.id!!)).thenReturn(Optional.of(member))
//
//        // Mocking toEntity method (assuming it's implemented in OrderDTO)
//        val orderEntity = Orders(totalPrice = 100, member = member)
//        whenever(orderRepository.save(Mockito.any(Orders::class.java))).thenReturn(orderEntity)
//
//        // When
//        val result = orderService.createOrder(orderDTO, member.id!!)
//
//        // Then
//        assertNotNull(result)
//        assertEquals(member.id, result.member.id)
//        assertEquals(100, result.totalPrice)
//    }
//
//    @Test
//    fun `read should return orderDTO when order exists`() {
//        // Given
//        val orderId = 1L
//        val order = Orders(totalPrice = 100, member = Member(/* ... */))
//        whenever(orderRepository.findById(orderId)).thenReturn(Optional.of(order))
//
//        // When
//        val result = orderService.read(orderId)
//
//        // Then
//        assertNotNull(result)
//        assertEquals(order.totalPrice, result?.totalPrice)
//    }
//
//    @Test
//    fun `read should throw OrderException when order does not exist`() {
//        // Given
//        val orderId = 1L
//        whenever(orderRepository.findById(orderId)).thenReturn(Optional.empty())
//
//        // When & Then
//        assertThrows<OrderException> {
//            orderService.read(orderId)
//        }
//    }
//
//    @Test
//    fun `delete should remove order when order exists`() {
//        // Given
//        val orderId = 1L
//        val order = Orders(totalPrice = 100, member = Member(/* ... */))
//        whenever(orderRepository.findById(orderId)).thenReturn(Optional.of(order))
//
//        // When
//        orderService.delete(orderId)
//
//        // Then
//        Mockito.verify(orderRepository).delete(order)
//    }
//
//    @Test
//    fun `getList should return paginated order list`() {
//        // Given
//        val member = Member(loginId = "testUser", email = "test@example.com", pw = "password", name = "Test User")
//        val pageable: Pageable = PageRequest.of(0, 10, Sort.by("id").descending())
//        val orderPage = PageImpl(listOf(Orders(/* ... */)), pageable, 1)
//
//        whenever(memberRepository.findById(member.id!!)).thenReturn(Optional.of(member))
//        whenever(orderRepository.findByMember(member, pageable)).thenReturn(orderPage)
//
//        // When
//        val result = orderService.getList(OrderDTO.PageRequestDTO(/* ... */), member.id!!)
//
//        // Then
//        assertEquals(1, result.totalElements)
//        assertEquals(1, result.content.size)
//    }
//
//    @Test
//    fun `getMonthlyOrderSummary should return summary when orders exist`() {
//        // Given
//        val memberId = 1L
//        val member = Member(loginId = "testUser", email = "test@example.com", pw = "password", name = "Test User")
//        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
//
//        val results = listOf(arrayOf("2023-10", 300))
//        whenever(orderRepository.getMonthlyTotalPrice(member)).thenReturn(results)
//
//        // When
//        val summary = orderService.getMonthlyOrderSummary(memberId)
//
//        // Then
//        assertEquals(1, summary.size)
//        assertEquals("2023-10", summary[0]["orderMonth"])
//        assertEquals(300, summary[0]["totalPrice"])
//    }
//
//    @Test
//    fun `getMonthlyAveragePrices should return average prices`() {
//        // Given
//        val orderItems = listOf(
//            OrderItem(/* ... */), // Fill with valid data
//            OrderItem(/* ... */)  // Fill with valid data
//        )
//        whenever(orderItemRepository.findAll()).thenReturn(orderItems)
//
//        // When
//        val averagePrices = orderService.getMonthlyAveragePrices()
//
//        // Then
//        assertNotNull(averagePrices)
//        // Add more assertions based on expected results
//    }
//}
