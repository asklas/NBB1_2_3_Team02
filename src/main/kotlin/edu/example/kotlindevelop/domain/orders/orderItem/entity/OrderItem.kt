package edu.example.kotlindevelop.domain.orders.orderItem.entity


import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import edu.example.kotlindevelop.domain.orders.orders.entity.Orders
import edu.example.kotlindevelop.domain.product.entity.Product
import org.modelmapper.internal.bytebuddy.build.ToStringPlugin
import org.springframework.data.jpa.domain.AbstractPersistable_.id


@Entity
@Table(name = "orderItem")

//open class OrderItem(
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    @JsonIgnore
    var orders: Orders,

    val quantity: Int,
    val price: Int
) {


    fun changeOrder(newOrders: Orders) {
        this.orders = newOrders
    }

    override fun toString(): String {
        return "OrderItem(id=$id, product=$product, orders=$orders, quantity=$quantity, price=$price)"
    }
}