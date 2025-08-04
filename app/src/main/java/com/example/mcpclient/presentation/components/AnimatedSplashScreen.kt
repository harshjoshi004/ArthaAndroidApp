package com.example.mcpclient.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcpclient.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun AnimatedSplashScreen(
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationPhase by remember { mutableIntStateOf(0) }
    
    // Animation values
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val logoScale by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    
    val textAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0f,
        animationSpec = tween(1000),
        label = "textAlpha"
    )
    
    LaunchedEffect(Unit) {
        delay(500)
        animationPhase = 1
        delay(1000)
        animationPhase = 2
        delay(2000)
        onAnimationComplete()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF1A1A2E),
                        DarkBackground
                    ),
                    radius = 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated background particles
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawAnimatedParticles(rotation)
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo
            Canvas(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale * pulseScale)
            ) {
                drawAnimatedLogo(rotation)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name with fade in
            Text(
                text = "ARTHA",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary.copy(alpha = textAlpha),
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha
                    scaleX = textAlpha
                    scaleY = textAlpha
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Agentic Financial Intelligence",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary.copy(alpha = textAlpha * 0.8f)
            )
        }
    }
}

private fun DrawScope.drawAnimatedParticles(rotation: Float) {
    val particleCount = 50
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    repeat(particleCount) { i ->
        val angle = (i * 360f / particleCount) + rotation
        val radius = (size.minDimension / 3) + sin(rotation * 0.01f + i) * 50
        
        val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * radius
        val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * radius
        
        val alpha = (sin(rotation * 0.02f + i) + 1) / 2
        val particleSize = 2f + sin(rotation * 0.03f + i) * 2f
        
        drawCircle(
            color = ElectricBlue.copy(alpha = alpha * 0.6f),
            radius = particleSize,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawAnimatedLogo(rotation: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = size.minDimension / 3
    
    // Outer ring
    rotate(rotation) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    ElectricBlue,
                    NeonGreen,
                    PurpleGlow,
                    ElectricBlue
                )
            ),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 8.dp.toPx())
        )
    }
    
    // Inner ring
    rotate(-rotation * 0.7f) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    PurpleGlow,
                    ElectricBlue,
                    NeonGreen,
                    PurpleGlow
                )
            ),
            radius = radius * 0.6f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 6.dp.toPx())
        )
    }
    
    // Center core
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                ElectricBlue,
                Color.Transparent
            )
        ),
        radius = radius * 0.3f,
        center = Offset(centerX, centerY)
    )
}
