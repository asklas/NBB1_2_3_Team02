package edu.example.kotlindevelop.domain.product.exception

import org.springframework.http.HttpStatus

enum class ProductException(val message: String, val status: HttpStatus) {
    PRODUCT_NOT_FOUND("해당 식재료를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_ALREADY_EXIST("이미 등록된 식재료입니다.", HttpStatus.CONFLICT);

    fun getProductException(): ProductTaskException {
        return ProductTaskException(message, status)
    }
}