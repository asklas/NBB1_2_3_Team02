package edu.example.kotlindevelop.domain.orders.orderItem.repository

import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


interface OrderItemRepository : JpaRepository<OrderItem?, Long?>
