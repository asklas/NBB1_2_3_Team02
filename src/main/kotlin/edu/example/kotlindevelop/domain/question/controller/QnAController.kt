package edu.example.kotlindevelop.domain.question.controller

import edu.example.kotlindevelop.domain.question.dto.*
import edu.example.kotlindevelop.domain.question.service.QnAService
import edu.example.kotlindevelop.global.security.SecurityUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/qna")
class QnAController(private val qnaService: QnAService) {

    @PostMapping("/post")
    fun createQuestion(
        @AuthenticationPrincipal user: SecurityUser,
        @RequestBody dto: QnACreateRequestDto): ResponseEntity<QnAResponseDto> {
        dto.userId = user.id
        val createdQuestion = qnaService.createQuestion(dto)
        return ResponseEntity(createdQuestion, HttpStatus.CREATED)
    }

    @GetMapping("/")
    fun getMyQuestion(@AuthenticationPrincipal user: SecurityUser): ResponseEntity<QnAListResponseDto> {
        val question = qnaService.getMyQuestion(user.id)
        return ResponseEntity.ok(question)
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: Long): ResponseEntity<QnAResponseDto> {
        val question = qnaService.getQuestion(id)
        return ResponseEntity.ok(question)
    }

    @GetMapping("/all")
    fun getAllQuestions(): ResponseEntity<QnAListResponseDto> {
        val questions = qnaService.getAllQuestions()
        return ResponseEntity.ok(questions)
    }

    @PutMapping("/{id}")
    fun updateQuestion(@PathVariable id: Long, @RequestBody dto: QnAUpdateRequestDto): ResponseEntity<QnAResponseDto> {
        val updatedQuestion = qnaService.updateQuestion(id, dto)
        return ResponseEntity.ok(updatedQuestion)
    }

    @DeleteMapping("/{id}")
    fun deleteQuestion(@PathVariable id: Long): ResponseEntity<Any> {
        qnaService.deleteQuestion(id)
        return ResponseEntity.ok(java.util.Map.of("result", "deleted"))
    }
}
