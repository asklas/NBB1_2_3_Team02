package edu.example.kotlindevelop.domain.question.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class QnA (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 255)
    var title: String,

    @Column(length = 1000)
    var description: String,

    @Column(nullable = false)
    val userId: Long,

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    var modifiedAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    var status: QuestionStatus = QuestionStatus.UNANSWERED
)

enum class QuestionStatus {
    ACTIVE,
    INACTIVE,
    DELETED,
    ANSWERED,
    IN_PROGRESS,
    UNANSWERED,
}
