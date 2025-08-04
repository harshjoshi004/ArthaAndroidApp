package com.example.mcpclient.data.models

/**
 * Data classes for Firebase chat functionality
 */

data class ChatMessage(
    val id: String = "",
    val llm_response: String = "",
    val query_user: String = "",
    val timestamps: Long = 0L,
    val llm_thinking: String = "", // AI thinking process
    // UI-friendly properties
    val isPseudo: Boolean = false // For animation purposes
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", 0L, "", false)
    
    // Helper properties for UI
    val isUser: Boolean get() = query_user.isNotEmpty()
    val content: String get() = if (isUser) query_user else llm_response
    val timestamp: Long get() = timestamps
    val hasThinking: Boolean get() = llm_thinking.isNotEmpty()
    val isThinkingOnly: Boolean get() = llm_thinking.isNotEmpty() && llm_response.isEmpty()
}

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
