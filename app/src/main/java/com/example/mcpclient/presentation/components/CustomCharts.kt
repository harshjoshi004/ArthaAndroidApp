package com.example.mcpclient.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcpclient.ui.theme.*
import kotlin.math.*

@Composable
fun AnimatedLineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = ElectricBlue,
    title: String = ""
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "lineProgress"
    )

    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            drawAnimatedLineChart(data, animatedProgress, lineColor)
        }
    }
}

@Composable
fun AnimatedDonutChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(ElectricBlue, NeonGreen, PurpleGlow, OrangeGlow),
    title: String = ""
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "donutProgress"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawAnimatedDonutChart(data, animatedProgress, colors)
            }

            // Center text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "₹${data.sumOf { it.second.toDouble() }.toInt()}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
            }
        }

        // Legend
        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(data.size) { index ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = colors[index % colors.size],
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = data[index].first,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedBarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    barColor: Color = NeonGreen,
    title: String = ""
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "barProgress"
    )

    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            drawAnimatedBarChart(data, animatedProgress, barColor)
        }
    }
}

private fun DrawScope.drawAnimatedLineChart(
    data: List<Float>,
    progress: Float,
    lineColor: Color
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOrNull() ?: 1f
    val minValue = data.minOrNull() ?: 0f
    val range = maxValue - minValue

    val stepX = size.width / (data.size - 1)
    val points = data.mapIndexed { index, value ->
        val x = index * stepX
        val y = size.height - ((value - minValue) / range) * size.height
        Offset(x, y)
    }

    // Draw animated line
    val path = Path()
    if (points.isNotEmpty()) {
        path.moveTo(points[0].x, points[0].y)

        val animatedPointCount = (points.size * progress).toInt().coerceAtLeast(1)
        for (i in 1 until animatedPointCount) {
            path.lineTo(points[i].x, points[i].y)
        }
    }

    // Draw gradient fill
    val fillPath = Path().apply {
        addPath(path)
        lineTo(size.width * progress, size.height)
        lineTo(0f, size.height)
        close()
    }

    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                lineColor.copy(alpha = 0.3f),
                Color.Transparent
            )
        )
    )

    // Draw line
    drawPath(
        path = path,
        color = lineColor,
        style = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Draw animated points
    val animatedPointCount = (points.size * progress).toInt()
    for (i in 0 until animatedPointCount) {
        drawCircle(
            color = lineColor,
            radius = 6.dp.toPx(),
            center = points[i]
        )
        drawCircle(
            color = Color.White,
            radius = 3.dp.toPx(),
            center = points[i]
        )
    }
}

private fun DrawScope.drawAnimatedDonutChart(
    data: List<Pair<String, Float>>,
    progress: Float,
    colors: List<Color>
) {
    val total = data.sumOf { it.second.toDouble() }.toFloat()
    val strokeWidth = 40.dp.toPx()
    val radius = (size.minDimension - strokeWidth) / 2
    val center = Offset(size.width / 2, size.height / 2)

    var startAngle = -90f

    data.forEachIndexed { index, (_, value) ->
        val sweepAngle = (value / total) * 360f * progress
        val color = colors[index % colors.size]

        // Draw arc with consistent style
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(
                center.x - radius,
                center.y - radius
            ),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Butt // Changed to Butt for consistent look
            )
        )

        startAngle += sweepAngle
    }
}

private fun DrawScope.drawAnimatedBarChart(
    data: List<Pair<String, Float>>,
    progress: Float,
    barColor: Color
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOf { it.second }
    val barWidth = size.width / data.size * 0.7f
    val spacing = size.width / data.size * 0.3f

    data.forEachIndexed { index, (label, value) ->
        val barHeight = (value / maxValue) * size.height * 0.8f * progress
        val x = index * (barWidth + spacing) + spacing / 2
        val y = size.height - barHeight

        // Draw bar with gradient
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    barColor,
                    barColor.copy(alpha = 0.7f)
                )
            ),
            topLeft = Offset(x, y),
            size = Size(barWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
        )

        // Draw value on top
        if (progress > 0.8f) {
            drawContext.canvas.nativeCanvas.drawText(
                "₹${value.toInt()}",
                x + barWidth / 2,
                y - 10.dp.toPx(),
                android.graphics.Paint().apply {
                    color = TextPrimary.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
