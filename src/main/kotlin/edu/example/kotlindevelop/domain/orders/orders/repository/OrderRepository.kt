package edu.example.kotlindevelop.domain.orders.orders.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.orders.orders.dto.OrderDTO
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Orders, Long> {
    fun findByMember(member: Member?, pageable: Pageable?): Page<Orders?>?

    @Query(
        "SELECT FORMATDATETIME(o.createdAt, 'yyyy-MM') AS orderMonth, SUM(o.totalPrice) AS totalPrice "
                + "FROM Orders o "
                + "WHERE o.member = :member " // 특정 회원 필터링
                + "GROUP BY FORMATDATETIME(o.createdAt, 'yyyy-MM') "
                + "ORDER BY FORMATDATETIME(o.createdAt, 'yyyy-MM')"
    )
    fun getMonthlyTotalPrice(@Param("member") member: Member?): List<Array<Any?>?>?

    fun findByMemberIdAndCreatedAtBetween(memberId: Long, createdAt: LocalDateTime, createdAt2: LocalDateTime): List<Orders>

    @Query("""
        SELECT o FROM Orders o
        JOIN o.orderItems oi
        WHERE FUNCTION('MONTH', o.createdAt) = :month 
        AND o.member = :member
        GROUP BY o.id
        ORDER BY COUNT(oi.id) DESC
    """)
    fun findOrdersByMemberAndMonthWithItemCountDesc(
        @Param("member") member: Member?,
        @Param("month") month: Int,
        pageable: Pageable
    ): Page<Orders>
} //    @Query("SELECT o FROM Orders o WHERE o.member = :member")
//    List<Orders> findAll(@Param("member") Member member);



