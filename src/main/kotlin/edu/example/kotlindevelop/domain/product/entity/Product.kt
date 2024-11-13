package edu.example.kotlindevelop.domain.product.entity

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.orders.orderItem.entity.OrderItem
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "product")
class Product(
    var name: String,
    @ManyToOne
    @JoinColumn(name = "member_id")
    var maker: Member? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    var lossRates: MutableList<LossRate> = mutableListOf()

    // 보조 생성자 추가
    constructor(id: Long?, maker: Member?) : this(name = "", maker = maker) {
    }

    fun addLossRate(lossRate: LossRate) {
        lossRates.add(lossRate)
        lossRate.product = this
    }

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL])
    var orderItems: MutableList<OrderItem> = mutableListOf()
//    val orderItems: List<OrderItem> = mutableListOf()
}


