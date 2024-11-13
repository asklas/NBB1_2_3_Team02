package edu.example.kotlindevelop.domain.product.controller
import edu.example.kotlindevelop.domain.product.dto.LossRateDTO
import edu.example.kotlindevelop.domain.product.dto.ProductDTO
import edu.example.kotlindevelop.domain.product.service.ProductService
import edu.example.kotlindevelop.global.security.SecurityUser
import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private final val productService: ProductService
) {
    // 식재료 등록
    @PostMapping("/register")
    fun register(
        @AuthenticationPrincipal user: SecurityUser,
        @Validated @RequestBody request: ProductDTO.CreateProductRequestDto
    ) : ResponseEntity<ProductDTO.CreateProductRequestDto> {
        val id: Long = user.id
        return ResponseEntity.ok(productService.insert(request, id))
    }

    // 식재료 추가 등록
    @PostMapping("/loss")
    fun addLoss(
        @AuthenticationPrincipal user: SecurityUser,
        @Validated @RequestBody request: LossRateDTO.LossRateRequestDTO
    ) : ResponseEntity<LossRateDTO.LossRateRequestDTO> {
        val id : Long = user.id
        return ResponseEntity.ok(productService.addLoss(request, id))
    }

    // 사용자용 상품 목록 전체 조회
    @GetMapping
    fun getList(
        @AuthenticationPrincipal user: SecurityUser,
        request: ProductDTO.PageRequestDto
    ) : ResponseEntity<Page<ProductDTO.ProductResponseDto>> {
        val memberId: Long = user.id
        val productDtoPage: Page<ProductDTO.ProductResponseDto> = productService.getPersonalProducts(request, memberId)
        return ResponseEntity.ok(productDtoPage)
    }

    // 사용자용 상품 이름 검색
    @GetMapping("/search")
    fun searchPersonalProducts(
        @RequestParam keyword: String,
        @AuthenticationPrincipal user: SecurityUser,
        request: ProductDTO.PageRequestDto
    ): ResponseEntity<Page<ProductDTO.ProductResponseDto>> {
        val memberId: Long = user.id
        val productDtoPage: Page<ProductDTO.ProductResponseDto> = productService.searchPersonalProducts(keyword, request, memberId)
        return ResponseEntity.ok(productDtoPage)
    }

    // 그래프처리
    @GetMapping("/loss/{name}")
    fun getAverageLossStatistics(
        @AuthenticationPrincipal user: SecurityUser,
        @PathVariable name: String,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate
    ): ResponseEntity<List<ProductDTO.AverageResponseDTO>> {
        val memberId = user.id
        val statistics = productService.getAverageStatistics(memberId, name, startDate, endDate)
        return ResponseEntity.ok(statistics)
    }


}