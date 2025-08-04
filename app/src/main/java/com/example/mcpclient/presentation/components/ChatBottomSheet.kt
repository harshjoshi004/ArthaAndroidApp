package com.example.mcpclient.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mcpclient.data.models.ChatSession
import com.example.mcpclient.presentation.viewmodel.McpViewModel
import com.example.mcpclient.ui.theme.DarkBackground
import com.example.mcpclient.ui.theme.DarkSurface
import com.example.mcpclient.ui.theme.ElectricBlue
import com.example.mcpclient.ui.theme.NeonGreen
import com.example.mcpclient.ui.theme.TextPrimary
import com.example.mcpclient.ui.theme.TextSecondary

@Composable
fun ChatBottomSheet(
    viewModel: McpViewModel,
    userId: String,
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(userId) {
        viewModel.observeUserChats(userId)
    }

    Column(
        modifier = modifier
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    DarkBackground,
                    Color(0x6A00FF88),
                    DarkBackground
                )
            ))
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with New Chat button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Here are ${userId.takeLast(4)}'s Chats..",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            
            // New Chat Button
            AnimatedCard(
                modifier = Modifier.clickable {
                    // Generate new session ID using the same logic as ViewModel
                    val timestamp = System.currentTimeMillis()
                    val random = (1..15).map { ('a'..'z').random() }.joinToString("")
                    val newSessionId = "session_${timestamp}_$random"
                    
                    // Set this as the current session in ViewModel for new chats
                    viewModel.currentSessionId = newSessionId
                    
                    // Navigate to the chat details screen
                    onChatClick(newSessionId)
                },
                glowColor = ElectricBlue
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "New Chat",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextPrimary
                    )
                }
            }
        }

        // Chat list
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            val userChats = viewModel.userChats
            
            if (userChats == null) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = NeonGreen,
                        modifier = Modifier.size(48.dp).fillMaxSize()
                    )
                }
            } else {
                val chatSessions = userChats.getChatSessions()
                
                if (chatSessions.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No chats found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Start a conversation to see your chats here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeonGreen,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Chat list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(chatSessions) { chatSession ->
                            ChatCard(
                                chatSession = chatSession,
                                onClick = { onChatClick(chatSession.sessionId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatCard(
    chatSession: ChatSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
                //.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.clickable{onClick()}
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(50.dp)
                )

                Column {
                    Text(
                        text = getDisplaySessionId(chatSession.sessionId),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextPrimary
                    )
                    val lastMessage = chatSession.getLastMessage()
                    if (lastMessage != null) {
                        Text(
                            text = lastMessage.query_user.take(20) + if (lastMessage.query_user.length > 20) "..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
            IconButton(
                onClick = onClick
            ){
                Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.size(50.dp),
                    contentDescription = null,
                    tint = TextPrimary,
                )
            }
        }
    }
}

private fun getDisplaySessionId(sessionId: String): String {
    return if (sessionId.length > 9) {
        "Chat Id : ..${sessionId.takeLast(9)}"
    } else {
        sessionId
    }
}
