package edu.example.kotlindevelop.domain.product.repository

import edu.example.kotlindevelop.domain.product.entity.LossRate
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LossRateRepository : JpaRepository<LossRate, Long> {

    @Query(
        value = "SELECT * FROM loss_rate l WHERE l.member_id = :memberId AND l.product_id = :productId ORDER BY l.id DESC LIMIT 1",
        nativeQuery = true
    )
    fun findLatestProductByMakerAndName(
        @Param("memberId") memberId: Long?,
        @Param("productId") productId: Long?
    ): LossRate
}