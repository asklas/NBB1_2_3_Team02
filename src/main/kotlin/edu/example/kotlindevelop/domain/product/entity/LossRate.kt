package edu.example.kotlindevelop.domain.product.entity

import edu.example.kotlindevelop.domain.member.entity.Member
import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate


@Entity
@Table(name = "loss_rate")
@EntityListeners(AuditingEntityListener::class)
class LossRate(
    @ManyToOne
    @JoinColumn(name = "member_id")
    var maker: Member,

    @ManyToOne
    @JoinColumn(name = "product_id")
    var product: Product,

    @Column(nullable = false)
    var loss: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreatedDate
    @Column(name = "recorded_at", nullable = false, updatable = false)
    var recordedAt: LocalDate? = null

}
