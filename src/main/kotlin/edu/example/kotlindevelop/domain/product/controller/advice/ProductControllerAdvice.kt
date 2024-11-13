package edu.example.kotlindevelop.domain.product.controller.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["edu.example.kotlindevelop.domain.product"])
class ProductControllerAdvice {
    // 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidException(e: MethodArgumentNotValidException): ResponseEntity<String> {
        return ResponseEntity(e.bindingResult.fieldErrors[0].defaultMessage, HttpStatus.BAD_REQUEST)
    }
}