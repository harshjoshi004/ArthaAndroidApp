package com.example.mcpclient.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mcpclient.ui.theme.*

@Composable
fun AnimatedCard(
    modifier: Modifier = Modifier,
    glowColor: Color = ElectricBlue,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cardGlowAlpha"
    )
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = glowColor.copy(alpha = glowAlpha),
                spotColor = glowColor.copy(alpha = glowAlpha)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkCard.copy(alpha = 0.9f),
                        DarkSurface.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        content()
    }
}
