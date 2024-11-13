package edu.example.kotlindevelop.domain.product.entity

interface ProductProjection {
    fun getProductId(): Long
    fun getProductName(): String
    fun getLatestLossRate(): Int
}