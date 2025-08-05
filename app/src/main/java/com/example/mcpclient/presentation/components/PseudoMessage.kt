package com.example.mcpclient.presentation.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcpclient.ui.theme.DarkSurface
import com.example.mcpclient.ui.theme.ElectricBlue
import com.example.mcpclient.ui.theme.NeonGreen
import com.example.mcpclient.ui.theme.PurpleGlow
import com.example.mcpclient.ui.theme.TextPrimary
import com.example.mcpclient.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class Agent(
    val name: String,
    val description: String,
    val tier: String,
    val duration: Long // Duration to stay highlighted in milliseconds
)

data class TierGroup(
    val tierName: String,
    val agents: List<Agent>,
    val color: Color
)

data class TierHeader(
    val name: String,
    val y: Float,
    val color: Color
)

@Composable
fun AgentRoadmapAnimation(
    isVisible: Boolean,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val agents = remember {
        listOf(
            Agent("Data Integration Agent", "Unifying your scattered financial life across 18+ sources", "Foundation Tier", 800L),
            Agent("Core Financial Advisor", "Translating your life dreams into actionable strategies", "Foundation Tier", 700L),
            Agent("Trust & Transparency Agent", "Explaining every recommendation with complete transparency", "Foundation Tier", 750L),
            Agent("Risk Profiling Agent", "Detecting when you're about to make emotional financial decisions", "Intelligence Tier", 850L),
            Agent("Anomaly Detection Agent", "Spotting unusual patterns before they become problems", "Intelligence Tier", 700L),
            Agent("Regional Investment Agent", "Knowing Pune real estate outperforms Mumbai rental yield", "Intelligence Tier", 900L),
            Agent("Debt Management Agent", "Optimizing loans considering your grandmother's medical needs", "Strategic Tier", 750L),
            Agent("Wealth Transfer Agent", "Planning inheritance while you're building wealth", "Strategic Tier", 800L),
            Agent("Cultural Events Agent", "Budgeting for Diwali, weddings, and family obligations", "Strategic Tier", 650L),
            Agent("Illiquid Asset Agent", "Turning your dormant gold into working capital", "Strategic Tier", 800L)
        )
    }

    // Group agents by tier for better organization
    val tierGroups = remember {
        listOf(
            TierGroup("Foundation Tier", agents.filter { it.tier == "Foundation Tier" }, PurpleGlow),
            TierGroup("Intelligence Tier", agents.filter { it.tier == "Intelligence Tier" }, ElectricBlue),
            TierGroup("Strategic Tier", agents.filter { it.tier == "Strategic Tier" }, NeonGreen)
        )
    }

    var currentAgentIndex by remember { mutableStateOf(-1) }
    var animationProgress by remember { mutableStateOf(0f) }
    var isAnimationRunning by remember { mutableStateOf(false) }

    // Animation progress for the flowing beam - smoother and faster
    val beamProgress by animateFloatAsState(
        targetValue = if (isAnimationRunning) 1f else 0f,
        animationSpec = tween(
            durationMillis = 8000, // Reduced from 12000 for faster animation
            easing = EaseInOutCubic // Smoother easing instead of linear
        ),
        finishedListener = {
            if (it == 1f) {
                onAnimationComplete()
                isAnimationRunning = false
            }
        }
    )

    // Start animation when visible
    LaunchedEffect(isVisible) {
        if (isVisible) {
            isAnimationRunning = true
            animationProgress = 0f
            currentAgentIndex = -1

            // Animate through each agent
            for (i in agents.indices) {
                currentAgentIndex = i
                delay(agents[i].duration)
            }

            // Keep the last agent highlighted for a bit longer
            delay(500L)
            currentAgentIndex = -1
        }
    }

    if (isVisible) {
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
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    NeonGreen,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                        Text(
                            text = "Processing your request through our AI agents...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Agent Roadmap - Increased height to accommodate tier headers
                    AgentRoadmapCanvas(
                        agents = agents,
                        tierGroups = tierGroups,
                        currentAgentIndex = currentAgentIndex,
                        beamProgress = beamProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1200.dp) // Increased to accommodate tier headers
                    )
                }
            }
        }
    }
}

@Composable
private fun AgentRoadmapCanvas(
    agents: List<Agent>,
    tierGroups: List<TierGroup>,
    currentAgentIndex: Int,
    beamProgress: Float,
    modifier: Modifier = Modifier
) {
    val neonGreen = NeonGreen
    val electricBlue = ElectricBlue
    val purpleGlow = PurpleGlow
    val textPrimary = TextPrimary
    val textSecondary = TextSecondary
    val darkSurface = DarkSurface

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth * 0.15f

        // Calculate positions for agents and tier headers
        val tierHeaderHeight = 40.dp.toPx()
        val agentHeight = 100.dp.toPx() // Space allocated per agent
        val tierSpacing = 20.dp.toPx() // Extra space between tiers

        var currentY = 50.dp.toPx() // Starting position
        val agentPositions = mutableListOf<Float>()
        val tierHeaderPositions = mutableListOf<TierHeader>()

        // Calculate all positions first
        tierGroups.forEach { tierGroup ->
            // Add tier header position
            tierHeaderPositions.add(TierHeader(tierGroup.tierName, currentY, tierGroup.color))
            currentY += tierHeaderHeight

            // Add agent positions for this tier
            tierGroup.agents.forEach { _ ->
                agentPositions.add(currentY)
                currentY += agentHeight
            }

            // Add spacing between tiers
            currentY += tierSpacing
        }

        // Draw tier headers first
        tierHeaderPositions.forEach { tierHeader ->
            // Tier header background line
            drawLine(
                color = tierHeader.color.copy(alpha = 0.3f),
                start = Offset(centerX - 30.dp.toPx(), tierHeader.y),
                end = Offset(canvasWidth - 40.dp.toPx(), tierHeader.y),
                strokeWidth = 1.dp.toPx()
            )

            // Tier header text
            drawContext.canvas.nativeCanvas.apply {
                val tierPaint = android.graphics.Paint().apply {
                    color = tierHeader.color.toArgb()
                    textSize = 16.sp.toPx()
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                drawText(
                    tierHeader.name.uppercase(),
                    centerX + 40.dp.toPx(),
                    tierHeader.y + 5.dp.toPx(),
                    tierPaint
                )
            }
        }

        // Draw connecting lines and flowing beam between agents
        for (i in 0 until agentPositions.size - 1) {
            val startY = agentPositions[i]
            val endY = agentPositions[i + 1]

            // Base connection line
            drawLine(
                color = Color(0x40FFFFFF),
                start = Offset(centerX, startY),
                end = Offset(centerX, endY),
                strokeWidth = 2.dp.toPx()
            )

            // Flowing beam effect - optimized calculation
            if (beamProgress > 0f) {
                val segmentProgress = ((beamProgress * agents.size) - i).coerceIn(0f, 1f)
                if (segmentProgress > 0f) {
                    val beamLength = 0.4f // Shorter beam for smoother effect
                    val beamStart = startY + (endY - startY) * maxOf(0f, segmentProgress - beamLength)
                    val beamEnd = startY + (endY - startY) * segmentProgress

                    if (beamEnd > beamStart) {
                        // Smoother gradient with better alpha transitions
                        drawLine(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    neonGreen.copy(alpha = 0.6f),
                                    electricBlue.copy(alpha = 0.9f),
                                    neonGreen,
                                    Color.Transparent
                                )
                            ),
                            start = Offset(centerX, beamStart),
                            end = Offset(centerX, beamEnd),
                            strokeWidth = 4.dp.toPx(), // Slightly thinner for smoother look
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // Draw agents
        agents.forEachIndexed { index, agent ->
            val y = agentPositions[index]
            val isActive = index == currentAgentIndex
            val hasBeenProcessed = beamProgress * agents.size > index

            // Agent circle with glow effect
            val circleRadius = if (isActive) 24.dp.toPx() else 18.dp.toPx()
            val circleColor = when {
                isActive -> neonGreen
                hasBeenProcessed -> electricBlue
                else -> Color(0x60FFFFFF)
            }

            // Glow effect for active agent
            if (isActive) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            neonGreen.copy(alpha = 0.6f),
                            neonGreen.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        radius = circleRadius * 2f
                    ),
                    radius = circleRadius * 2f,
                    center = Offset(centerX, y)
                )
            }

            // Agent circle
            drawCircle(
                color = circleColor,
                radius = circleRadius,
                center = Offset(centerX, y)
            )

            // Inner circle
            drawCircle(
                color = if (isActive) Color.White else darkSurface,
                radius = circleRadius * 0.6f,
                center = Offset(centerX, y)
            )

            // Agent number
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = if (isActive) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                    textSize = 14.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                drawText(
                    "${index + 1}",
                    centerX,
                    y + paint.textSize / 3,
                    paint
                )
            }
        }

        // Draw agent information with proper spacing
        agents.forEachIndexed { index, agent ->
            val y = agentPositions[index]
            val textStartX = canvasWidth * 0.25f
            val isActive = index == currentAgentIndex
            val hasBeenProcessed = beamProgress * agents.size > index

            drawContext.canvas.nativeCanvas.apply {
                // Agent name
                val namePaint = android.graphics.Paint().apply {
                    color = when {
                        isActive -> neonGreen.toArgb()
                        hasBeenProcessed -> electricBlue.toArgb()
                        else -> textSecondary.toArgb()
                    }
                    textSize = if (isActive) 16.sp.toPx() else 14.sp.toPx()
                    typeface = if (isActive) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                }
                drawText(
                    agent.name,
                    textStartX,
                    y - 15.dp.toPx(),
                    namePaint
                )

                // Agent description with proper word wrapping
                val descPaint = android.graphics.Paint().apply {
                    color = if (isActive) textPrimary.toArgb() else textSecondary.toArgb()
                    textSize = 12.sp.toPx()
                    typeface = android.graphics.Typeface.DEFAULT
                }

                val words = agent.description.split(" ")
                val maxWidth = canvasWidth - textStartX - 20.dp.toPx()
                var currentLine = ""
                var lineY = y + 10.dp.toPx()
                val lineSpacing = 16.dp.toPx()

                words.forEach { word ->
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    if (descPaint.measureText(testLine) <= maxWidth) {
                        currentLine = testLine
                    } else {
                        if (currentLine.isNotEmpty()) {
                            drawText(currentLine, textStartX, lineY, descPaint)
                            lineY += lineSpacing
                        }
                        currentLine = word
                    }
                }
                if (currentLine.isNotEmpty()) {
                    drawText(currentLine, textStartX, lineY, descPaint)
                }
            }
        }
    }
}