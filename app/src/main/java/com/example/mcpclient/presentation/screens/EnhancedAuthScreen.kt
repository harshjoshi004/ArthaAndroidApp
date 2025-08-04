package com.example.mcpclient.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcpclient.presentation.components.AnimatedCard
import com.example.mcpclient.presentation.components.GlowingButton
import com.example.mcpclient.presentation.components.PhoneNumberDropdown
import com.example.mcpclient.presentation.viewmodel.McpViewModel
import com.example.mcpclient.ui.theme.*
import kotlin.math.*

@Composable
fun EnhancedAuthScreen(
    viewModel: McpViewModel,
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn) {
            onNavigateToMain()
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "authBackground")
    
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "backgroundAnimation"
    )
    
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
                    radius = 1200f
                )
            )
    ) {
        // Animated background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAnimatedBackground(animatedOffset)
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo section
            AnimatedCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = ElectricBlue
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Welcome to",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        Image(
                            painter = painterResource(com.example.mcpclient.R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(48.dp, 48.dp)
                                .clip(CircleShape)
                                .border(2.dp, PinkGlow, CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "ARTHA",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Agentic Financial Intelligence",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PurpleGlow
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Authentication form
            AnimatedCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = NeonGreen
            ) {
                Column {
                    Text(
                        text = "Select Your Phone Number",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    
                    PhoneNumberDropdown(
                        selectedPhoneNumber = viewModel.selectedPhoneNumber,
                        onPhoneNumberSelected = viewModel::updateSelectedPhoneNumber,
                        phoneNumbers = McpViewModel.ALLOWED_PHONE_NUMBERS,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                    
                    GlowingButton(
                        text = if (viewModel.isLoading) "Authenticating..." else "Sign In",
                        onClick = viewModel::signIn,
                        enabled = !viewModel.isLoading,
                        glowColor = ElectricBlue,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Error message
            viewModel.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(20.dp))
                
                AnimatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = ErrorRed
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawAnimatedBackground(offset: Float) {
    val particleCount = 30
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    repeat(particleCount) { i ->
        val angle = (i * 360f / particleCount) + offset
        val radius = (size.minDimension / 4) + sin(Math.toRadians((offset + i * 10).toDouble())).toFloat() * 100
        
        val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * radius
        val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * radius
        
        val alpha = (sin(Math.toRadians((offset * 2 + i * 20).toDouble())) + 1) / 2
        val particleSize = 1f + sin(Math.toRadians((offset * 3 + i * 15).toDouble())).toFloat() * 2f
        
        val colors = listOf(ElectricBlue, NeonGreen, PurpleGlow)
        val color = colors[i % colors.size]
        
        drawCircle(
            color = color.copy(alpha = alpha.toFloat() * 0.4f),
            radius = particleSize,
            center = Offset(x, y)
        )
    }
}
