package edu.example.kotlindevelop.domain.chat.entity

data class ChatMessage(
    val type: MessageType,
    val content: String?,
    val sender: String,
    val receiver: String
)

enum class MessageType {
    CHAT,
    JOIN,
    LEAVE
}