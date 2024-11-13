package edu.example.kotlindevelop.domain.question.dto


import edu.example.kotlindevelop.domain.question.entity.QnA
import edu.example.kotlindevelop.domain.question.entity.QuestionStatus
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class QnACreateRequestDto(
    @field:NotBlank(message = "제목은 필수 입력 값입니다.")
    val title: String,

    @field:NotBlank(message = "내용은 필수 입력 값입니다.")
    val description: String,

    var status: QuestionStatus = QuestionStatus.UNANSWERED,

    var userId: Long
) {
    fun toEntity(): QnA {
        return QnA(
            title = title,
            description = description,
            userId = userId,
            status = status,
        )
    }
}

data class QnAUpdateRequestDto(
    val id: Long,
    var title: String? = null,
    var description: String? = null,
    var status: QuestionStatus
)

data class QnAResponseDto(
    val id: Long,
    val title: String,
    val description: String,
    val userId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val status: QuestionStatus
) {
    constructor(qna: QnA) : this(
        id = qna.id ?: throw IllegalArgumentException("QnA ID cannot be null"),
        title = qna.title,
        description = qna.description,
        userId = qna.userId,
        createdAt = qna.createdAt,
        modifiedAt = qna.modifiedAt,
        status = qna.status
    )
}

data class QnADeleteRequestDto(
    val id: Long
)

data class QnAListResponseDto(
    val questions: List<QnAResponseDto>
)
