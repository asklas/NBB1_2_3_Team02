package edu.example.kotlindevelop.domain.product.repository

import edu.example.kotlindevelop.domain.member.entity.Member
import edu.example.kotlindevelop.domain.product.entity.Product
import edu.example.kotlindevelop.domain.product.entity.ProductProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface ProductRepository : JpaRepository<Product?, Long?> {
    fun findByMakerAndName(member: Member, productName: String?): Product? // 식재료 등록

    // 일치하는 상품명에 대한 개인의 평균 로스율 조회
    @Query("SELECT DATE(l.recordedAt),AVG(l.loss) FROM Product p JOIN LossRate l ON l.product.id = p.id " +
            "WHERE p.name = :name AND p.maker.id = :memberId AND l.recordedAt BETWEEN :startDate AND :endDate AND l.loss BETWEEN 0 AND 100 GROUP BY DATE(l.recordedAt)")
    fun findPersonalAverageByMakerAndName(
        @Param("memberId") memberId: Long,
        @Param("name") name: String,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    // 일치하는 상품명에 대한 전체 평균 로스율 조회
    @Query(
        "SELECT DATE(l.recordedAt), AVG(l.loss) FROM Product p JOIN LossRate l ON l.product.id = p.id " +
                "WHERE p.name = :name AND l.recordedAt BETWEEN :startDate AND :endDate AND l.loss BETWEEN 0 AND 100 " +
                "GROUP BY DATE(l.recordedAt)"
    )
    fun findAverageStatisticsByName(
        @Param("name") name: String?,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Array<Any>>

    // 사용자용 상품 목록 전체 조회
    @Query("""
        SELECT p.id AS productId, p.name AS productName, l.loss AS latestLossRate 
        FROM Product p LEFT JOIN p.lossRates l 
        WHERE p.maker.id = :memberId AND l.id = (
            SELECT MAX(l2.id) 
            FROM LossRate l2 
            WHERE l2.product.id = p.id
        ) 
        ORDER BY p.name
    """)
    fun findPersonalProducts(memberId: Long, pageable: Pageable): Page<ProductProjection>

    // 사용자용 상품 이름 검색
    @Query("""
        SELECT p.id AS productId, p.name AS productName, l.loss AS latestLossRate 
        FROM Product p LEFT JOIN p.lossRates l 
        WHERE p.name LIKE %:keyword% AND p.maker.id = :memberId AND l.id = (
            SELECT MAX(l2.id) 
            FROM LossRate l2 
            WHERE l2.product.id = p.id
        ) 
        ORDER BY p.name
    """)
    fun findPersonalProductsByKeyword(keyword: String, memberId: Long, pageable: Pageable): Page<ProductProjection>

    // 관리자용 상품 목록 전체 조회
    @Query("""
        SELECT p.id AS productId, p.name AS productName, l.loss AS latestLossRate 
        FROM Product p LEFT JOIN p.lossRates l 
        WHERE l.id = (
            SELECT MAX(l2.id) 
            FROM LossRate l2 
            WHERE l2.product.id = p.id
        ) 
        ORDER BY p.name
    """)
    fun findAllProducts(pageable: Pageable): Page<ProductProjection>

    // 관리자용 상품 이름 검색
    @Query("""
        SELECT p.id AS productId, p.name AS productName, l.loss AS latestLossRate 
        FROM Product p LEFT JOIN p.lossRates l 
        WHERE p.name LIKE %:keyword% AND l.id = (
            SELECT MAX(l2.id) 
            FROM LossRate l2 
            WHERE l2.product.id = p.id
        ) 
        ORDER BY p.name
    """)
    fun findProductsByKeyword(keyword: String, pageable: Pageable): Page<ProductProjection>
}