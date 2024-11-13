package edu.example.kotlindevelop.domain.chat.controller

import edu.example.kotlindevelop.domain.chat.entity.ChatMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatController {

    // 메시지 전송 (사용자 메시지 전송 시)
    @MessageMapping("/chat.sendMessage")
    fun sendMessage(chatMessage: ChatMessage): ChatMessage {
        // 관리자는 모든 메시지를 수신
        messagingTemplate.convertAndSend("/topic/admin", chatMessage)

        // 해당 사용자에게만 메시지 전달 (각 사용자의 채팅)
        messagingTemplate.convertAndSend("/topic/${chatMessage.receiver}", chatMessage)

        return chatMessage
    }

    // 사용자 추가 (유저가 채팅을 시작할 때)
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    fun addUser(chatMessage: ChatMessage): ChatMessage {
        return chatMessage
    }

    @Autowired
    private lateinit var messagingTemplate: SimpMessagingTemplate
}

