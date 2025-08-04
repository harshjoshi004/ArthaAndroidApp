package com.example.mcpclient.presentation.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mcpclient.data.models.ChatMessage
import com.example.mcpclient.presentation.viewmodel.McpViewModel
import com.example.mcpclient.presentation.components.AgentRoadmapAnimation
import com.example.mcpclient.ui.theme.DarkBackground
import com.example.mcpclient.ui.theme.DarkBorder
import com.example.mcpclient.ui.theme.DarkSurface
import com.example.mcpclient.ui.theme.ElectricBlue
import com.example.mcpclient.ui.theme.NeonGreen
import com.example.mcpclient.ui.theme.PurpleGlow
import com.example.mcpclient.ui.theme.TextPrimary
import com.example.mcpclient.ui.theme.TextSecondary
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailsScreen(
    viewModel: McpViewModel,
    sessionId: String,
    userId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Animation state
    var showAgentAnimation by remember { mutableStateOf(false) }

    // Start observing chat messages in real-time
    LaunchedEffect(sessionId, userId) {
        viewModel.observeChatMessages(userId, sessionId)
    }

    // Auto-scroll to bottom when new messages arrive or animation starts
    LaunchedEffect(viewModel.selectedChatSession?.messages?.size, showAgentAnimation) {
        if (viewModel.selectedChatSession?.messages?.isNotEmpty() == true || showAgentAnimation) {
            scope.launch {
                val itemCount = (viewModel.selectedChatSession?.messages?.size ?: 0) +
                        if (showAgentAnimation) 1 else 0
                if (itemCount > 0) {
                    listState.animateScrollToItem(itemCount - 1)
                }
            }
        }
    }

    val chatSession = viewModel.selectedChatSession
    val messages = chatSession?.messages?.values?.sortedBy { it.timestamps } ?: emptyList()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ChatTopBar(
            sessionId = sessionId,
            onNavigateBack = onNavigateBack
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (messages.isEmpty() && !showAgentAnimation) {
                EmptyChatState()
            } else {
                ChatMessagesList(
                    messages = messages,
                    listState = listState,
                    showAgentAnimation = showAgentAnimation,
                    onAnimationComplete = {
                        showAgentAnimation = false
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        ChatBottomBar(
            messageText = messageText,
            onMessageTextChange = { messageText = it },
            onSendMessage = {
                if (messageText.trim().isNotEmpty()) {
                    // Trigger the agent animation
                    showAgentAnimation = true

                    // Send the actual message
                    viewModel.sendMessage(userId, sessionId, messageText.trim())
                    messageText = ""
                    keyboardController?.hide()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    sessionId: String,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(DarkSurface)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Back Icon
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            // Profile Icon and Session Info
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(
                    text = "ARTHA: AI Assistant",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = getDisplaySessionId(sessionId),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ChatBottomBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Ask me anything about your finances...",
                        color = TextSecondary
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onSendMessage() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Color(0x4000FF88),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = ElectricBlue
                ),
                maxLines = 4
            )

            IconButton(
                onClick = onSendMessage,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ElectricBlue, PurpleGlow)
                        ),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ChatMessagesList(
    messages: List<ChatMessage>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    showAgentAnimation: Boolean,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    DarkBackground,
                    Color(0x4000FF88),
                    DarkBackground
                )
            )),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(messages) { message ->
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // User message
                    UserMessageBubble(
                        message = message.query_user,
                        timestamp = message.timestamps
                    )

                    // AI response
                    if (message.llm_response.isNotEmpty()) {
                        AIMessageBubble(
                            message = message.llm_response,
                            timestamp = message.timestamps
                        )
                    } else {
                        // Show typing indicator if AI hasn't responded yet
                        TypingIndicator()
                    }
                }
            }
        }

        // Agent Animation (shown as pseudo-message)
        if (showAgentAnimation) {
            item {
                AnimatedVisibility(
                    visible = showAgentAnimation,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(500))
                ) {
                    AgentRoadmapAnimation(
                        isVisible = showAgentAnimation,
                        onAnimationComplete = onAnimationComplete
                    )
                }
            }
        }

        // Add extra space at the bottom for better UX
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UserMessageBubble(
    message: String,
    timestamp: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = PurpleGlow
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 4.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                modifier = Modifier.shadow(4.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = formatTimestamp(timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, end = 8.dp)
            )
        }
    }
}

@Composable
private fun AIMessageBubble(
    message: String,
    timestamp: Long
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier
                .size(32.dp)
                .padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface
                ),
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                modifier = Modifier.shadow(4.dp)
            ) {

                MarkdownText(
                    markdown = message,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = formatTimestamp(timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp)
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier
                .size(32.dp)
                .padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = DarkSurface
            ),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            modifier = Modifier.shadow(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "AI is thinking",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = NeonGreen,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
private fun EmptyChatState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF0F0F23),
                        DarkBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val alpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(1000),
                label = "alphaAnimation"
            )

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier
                    .size(72.dp)
                    .alpha(alpha)
            )

            Text(
                text = "Start a conversation",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.alpha(alpha)
            )

            Text(
                text = "Ask me anything about your finances!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
}

private fun getDisplaySessionId(sessionId: String): String {
    return if (sessionId.length > 8) {
        "Chat ...${sessionId.takeLast(8)}"
    } else {
        "Chat $sessionId"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
