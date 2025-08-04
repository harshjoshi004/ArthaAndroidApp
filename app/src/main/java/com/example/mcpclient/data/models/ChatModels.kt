package com.example.mcpclient.data.models

/**
 * Data classes for Firebase chat functionality
 */

data class ChatMessage(
    val id: String = "",
    val llm_response: String = "",
    val query_user: String = "",
    val timestamps: Long = 0L
)

data class ChatSession(
    val sessionId: String = "",
    val sessionName: String = "",
    val messages: Map<String, ChatMessage> = emptyMap(),
    val lastMessageTime: Long = 0L
) {
    fun getDisplayName(): String {
        return if (sessionName.isNotBlank()) {
            sessionName
        } else {
            // Extract UUID from session ID for display
            val parts = sessionId.split("_")
            if (parts.size >= 3) {
                "Chat ${parts.last().take(8)}"
            } else {
                "Chat Session"
            }
        }
    }
    
    fun getLastMessage(): ChatMessage? {
        return messages.values.maxByOrNull { it.timestamps }
    }
}

data class UserChats(
    val userId: String = "",
    val chats: Map<String, Map<String, ChatMessage>> = emptyMap(),
    val financial_summary: String = ""
) {
    fun getChatSessions(): List<ChatSession> {
        return chats.map { (sessionId, messages) ->
            val chatMessages = messages.mapValues { (messageId, messageData) ->
                messageData.copy(id = messageId)
            }
            val lastMessageTime = chatMessages.values.maxOfOrNull { it.timestamps } ?: 0L
            
            ChatSession(
                sessionId = sessionId,
                messages = chatMessages,
                lastMessageTime = lastMessageTime
            )
        }.sortedByDescending { it.lastMessageTime }
    }
}
