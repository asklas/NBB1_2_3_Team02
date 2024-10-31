package edu.example.kotlindevelop.domain.product.repository

import edu.example.kotlindevelop.domain.product.entity.LossRate
import edu.example.kotlindevelop.domain.product.entity.Product
import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.member.repository.MemberRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class LossRateRepositoryTests {
    @Autowired
    private val productRepository: ProductRepository? = null

    @Autowired
    private val memberRepository: MemberRepository? = null

    @Autowired
    private val lossRateRepository: LossRateRepository? = null

    @Test
    fun 로스율등록테스트() {
        // Given
        val member: Member = Member(
            loginId = "membertest",
            pw = "qwer",
            name = "테스트",
            mImage = "아바타",
            email = "example@example.com"
        )
        val savedMember = memberRepository!!.save(member)

        val product:Product = Product(
            name = "양파",
            maker = savedMember
        )
        val savedProduct = productRepository!!.save(product)
        savedMember.productList.add(product)

        // When
        val lossRate: LossRate = LossRate(
            maker = savedMember,
            product = savedProduct,
            loss = 10,
        )
        val savedLossRate = lossRateRepository!!.save(lossRate)

        // Then
        println("member id: ${savedMember.id}")
        println("member name: ${savedMember.name}")
        println(savedMember.productList)

        println("product's member id: ${savedProduct.maker?.id}")
        println("loss rate for product: ${savedLossRate.loss}")

        assertNotNull(savedProduct)
        assertEquals(savedMember.name, savedProduct.maker?.name)
        assertEquals("양파", savedProduct.name)
        assertEquals(10, savedLossRate.loss)
    }
}