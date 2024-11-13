package edu.example.kotlindevelop.domain.question.repository

import edu.example.kotlindevelop.domain.question.entity.QnA
import edu.example.kotlindevelop.domain.question.entity.QuestionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface QnARepository : JpaRepository<QnA, Long> {
    // 사용자 ID로 질문 목록을 조회
    fun findByUserId(userId: Long): List<QnA>

    // 제목에 키워드가 포함된 질문을 조회
    fun findByTitleContaining(keyword: String): List<QnA>

    // 질문 상태로 질문을 조회
    fun findByStatus(status: QuestionStatus): List<QnA>
}
