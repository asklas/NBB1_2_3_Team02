package edu.example.kotlindevelop.domain.product.dto

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.Product
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.time.LocalDate


class ProductDTO {

    data class CreateProductRequestDto(
        @field:NotBlank(message = "식재료 이름은 필수 값입니다.")
        val name: String,

        @field:Min(value = 0, message = "로스율은 0 이상이어야 합니다.")
        @field:Max(value = 100, message = "로스율은 100 이하여야 합니다.")
        val loss: Int?
    ) {
        fun toEntity(member: Member): Product {
            val product = Product(name = name, maker = member)
            val lossRateValue = loss ?: 222
            val lossRate = LossRate(
                maker = member,
                product = product,
                loss = lossRateValue
            )

            product.addLossRate(lossRate)
            return product
        }
    }

    data class ProductResponseDto (
        val id: Long,
        val name: String,
        val loss: Int
    )

    data class PageRequestDto(
        private val page: Int = 0,
        private val size: Int = 5,
        private val sortField: String = "id",
        private val sortDirection: String = "ASC",
    ) {

        val pageable: Pageable
            get() {
                val sort = Sort.by(
                    Sort.Direction.fromString(
                        this.sortDirection
                    ), this.sortField
                )
                return PageRequest.of(this.page, this.size, sort)
            }
    }

    data class AverageResponseDTO(
        val dates: List<LocalDate>,
        val personalAverage: List<BigDecimal>,
        val allUsersAverage: List<BigDecimal>
    )
}
