package edu.example.kotlindevelop.domain.question.service

import edu.example.kotlindevelop.domain.question.dto.QnACreateRequestDto
import edu.example.kotlindevelop.domain.question.dto.QnAListResponseDto
import edu.example.kotlindevelop.domain.question.dto.QnAResponseDto
import edu.example.kotlindevelop.domain.question.dto.QnAUpdateRequestDto
import edu.example.kotlindevelop.domain.question.repository.QnARepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class QnAService(private val qnaRepository: QnARepository) {

    @Transactional
    fun createQuestion(dto: QnACreateRequestDto): QnAResponseDto {
        val qna = dto.toEntity()
        return QnAResponseDto(qnaRepository.save(qna))
    }

    fun getQuestion(id: Long): QnAResponseDto {
        val qna = qnaRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Question not found with id: $id") }
        return QnAResponseDto(qna)
    }

    fun getMyQuestion(id: Long): QnAListResponseDto {
        val qna = qnaRepository.findByUserId(id).map { QnAResponseDto(it) }
        return QnAListResponseDto(qna)
    }

    fun getAllQuestions(): QnAListResponseDto {
        val questions = qnaRepository.findAll().map { QnAResponseDto(it) }
        return QnAListResponseDto(questions)
    }

    @Transactional
    fun updateQuestion(id: Long, dto: QnAUpdateRequestDto): QnAResponseDto {
        val qna = qnaRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Question not found with id: $id") }

        dto.title?.let { qna.title = it }
        dto.description?.let { qna.description = it }
        dto.status.let { qna.status = it }
        qna.modifiedAt = LocalDateTime.now()

        return QnAResponseDto(qnaRepository.save(qna))
    }

    @Transactional
    fun deleteQuestion(id: Long) {
        if (!qnaRepository.existsById(id)) {
            throw IllegalArgumentException("Question not found with id: $id")
        }
        qnaRepository.deleteById(id)
    }
}