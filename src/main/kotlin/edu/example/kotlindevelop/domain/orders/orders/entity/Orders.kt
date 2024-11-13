package edu.example.kotlindevelop.domain.orders.orders.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import edu.example.kotlindevelop.domain.product.entity.Product
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
class Orders(
    var totalPrice: Long,

    @ManyToOne // Member와 관계를 설정
    @JoinColumn(name = "member_id") // 외래키 설정
    @JsonBackReference
    var member: Member
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreatedDate
    @Column(name = "created_at", updatable = false) // 생성일은 업데이트하지 않음
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm") // 초 단위 생략
    var createdAt: LocalDateTime? = null

    @OneToMany(mappedBy = "orders", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orderItems: MutableList<OrderItem> = ArrayList()

    fun addOrderItem(product: Product?, quantity: Int, price: Int) {
        val orderItem = OrderItem(
            orders = this,
            product = product,
            quantity = quantity,
            price = price
        )
        orderItems.add(orderItem)
        this.totalPrice += price.toLong() * quantity
    }
}
