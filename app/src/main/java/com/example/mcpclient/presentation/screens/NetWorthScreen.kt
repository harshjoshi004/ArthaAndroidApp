package com.example.mcpclient.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcpclient.presentation.components.*
import com.example.mcpclient.presentation.viewmodel.McpViewModel
import com.example.mcpclient.ui.theme.*
import kotlin.math.*

@Composable
fun NetWorthScreen(
    viewModel: McpViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fetch net worth data when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchNetWorth()
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF0F0F23),
                        DarkBackground
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            NetWorthHeader(onNavigateBack)
        }
        
        item {
            NetWorthSummary(viewModel)
        }
        
        item {
            NetWorthTrend()
        }
        
        item {
            AssetBreakdown()
        }
        
        item {
            LiabilityBreakdown()
        }
        
        item {
            MonthlyGrowth()
        }
        
        items(generateAssetDetails()) { asset ->
            AssetDetailCard(asset)
        }
    }
}

@Composable
private fun NetWorthHeader(onNavigateBack: () -> Unit) {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = NeonGreen
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Net Worth Analysis",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Complete financial overview",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.width(16.dp))
            
            GlowingButton(
                text = "Back",
                onClick = onNavigateBack,
                glowColor = NeonGreen
            )
        }
    }
}

@Composable
private fun NetWorthSummary(viewModel: McpViewModel) {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = ElectricBlue
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Net Worth",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (viewModel.isDataLoading) {
                CircularProgressIndicator(color = NeonGreen)
            } else {
                val totalNetWorth = viewModel.netWorthData?.netWorthResponse?.totalNetWorthValue?.let {
                    "₹${it.units.toDouble().div(100000).toInt()}.${((it.units.toDouble()?.rem(100000) ?: 96540).toInt() / 10000).toInt()}L"
                } ?: "₹12.96L"
                
                Text(
                    text = totalNetWorth,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonGreen
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val netWorthData = viewModel.netWorthData
                
                val totalAssets = netWorthData?.netWorthResponse?.assetValues?.sumOf {
                    it.value.units.toDouble()
                }?.let { "₹${(it / 100000).toInt()}L" } ?: "₹15.46L"
                
                // Calculate liabilities from credit card summaries
                val totalLiabilities = netWorthData?.accountDetailsBulkResponse?.accountDetailsMap?.values
                    ?.mapNotNull { it.creditCardSummary?.currentBalance?.units?.toDouble() }
                    ?.sum()?.let { "₹${(it / 100000).toInt()}L" } ?: "₹2.50L"
                
                NetWorthMetric("Assets", totalAssets, SuccessGreen)
                NetWorthMetric("Liabilities", totalLiabilities, ErrorRed)
                NetWorthMetric("Growth", "+18.5%", ElectricBlue)
            }
        }
    }
}

@Composable
private fun NetWorthMetric(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}

@Composable
private fun NetWorthTrend() {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = PurpleGlow
    ) {
        Column {
            Text(
                text = "Net Worth Trend (12 Months)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val trendData = listOf(
                850000f, 920000f, 980000f, 1050000f, 1120000f, 1180000f,
                1250000f, 1180000f, 1220000f, 1280000f, 1250000f, 1296540f
            )
            
            AnimatedLineChart(
                data = trendData,
                lineColor = PurpleGlow,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AssetBreakdown() {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = SuccessGreen
    ) {
        Column {
            Text(
                text = "Asset Breakdown",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val assetData = listOf(
                "Investments" to 876540f,
                "Savings Account" to 250000f,
                "Fixed Deposits" to 300000f,
                "EPF" to 195000f,
                "Real Estate" to 0f,
                "Gold" to 120000f
            )
            
            // Custom radial chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                drawRadialAssetChart(assetData)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Asset list
            assetData.forEach { (asset, value) ->
                if (value > 0) {
                    AssetRow(asset, "₹${value.toInt()}", getAssetColor(asset))
                }
            }
        }
    }
}

@Composable
private fun AssetRow(
    name: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}

private fun getAssetColor(asset: String): Color {
    return when (asset) {
        "Investments" -> ElectricBlue
        "Savings Account" -> NeonGreen
        "Fixed Deposits" -> PurpleGlow
        "EPF" -> OrangeGlow
        "Gold" -> Color(0xFFFFD700)
        else -> TextSecondary
    }
}

private fun DrawScope.drawRadialAssetChart(data: List<Pair<String, Float>>) {
    val total = data.sumOf { it.second.toDouble() }.toFloat()
    val center = Offset(size.width / 2, size.height / 2)
    val maxRadius = size.minDimension / 3
    
    data.forEachIndexed { index, (asset, value) ->
        if (value > 0) {
            val percentage = value / total
            val radius = maxRadius * (0.3f + percentage * 0.7f)
            val color = getAssetColor(asset)
            
            // Draw concentric circles
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = radius,
                center = center,
                style = Stroke(width = 20.dp.toPx())
            )
            
            // Draw value text
            val angle = index * 60f
            val textX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * (radius + 30.dp.toPx())
            val textY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * (radius + 30.dp.toPx())
            
            drawContext.canvas.nativeCanvas.drawText(
                "₹${(value / 100000).toInt()}L",
                textX,
                textY,
                android.graphics.Paint().apply {
                    this.color = color.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

@Composable
private fun LiabilityBreakdown() {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = ErrorRed
    ) {
        Column {
            Text(
                text = "Liabilities",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val liabilities = listOf(
                Triple("Home Loan", "₹1,80,000", "₹25,000/month"),
                Triple("Car Loan", "₹45,000", "₹8,500/month"),
                Triple("Credit Card", "₹25,000", "Min: ₹2,500")
            )
            
            liabilities.forEach { (name, outstanding, emi) ->
                LiabilityRow(name, outstanding, emi)
            }
        }
    }
}

@Composable
private fun LiabilityRow(
    name: String,
    outstanding: String,
    emi: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextPrimary
            )
            Text(
                text = outstanding,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = ErrorRed
            )
        }
        
        Text(
            text = emi,
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary
        )
    }
}

@Composable
private fun MonthlyGrowth() {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = OrangeGlow
    ) {
        Column {
            Text(
                text = "Monthly Growth Analysis",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val monthlyData = listOf(
                "Jan" to 45000f,
                "Feb" to 52000f,
                "Mar" to 38000f,
                "Apr" to 65000f,
                "May" to 48000f,
                "Jun" to 72000f
            )
            
            AnimatedBarChart(
                data = monthlyData,
                barColor = OrangeGlow,
                title = "Net Worth Growth by Month"
            )
        }
    }
}

data class AssetDetail(
    val name: String,
    val category: String,
    val value: String,
    val growth: String,
    val color: Color
)

private fun generateAssetDetails(): List<AssetDetail> {
    return listOf(
        AssetDetail("HDFC Savings Account", "Liquid Assets", "₹2,50,000", "+2.5%", NeonGreen),
        AssetDetail("SBI Fixed Deposit", "Fixed Income", "₹3,00,000", "+6.8%", ElectricBlue),
        AssetDetail("Mutual Fund Portfolio", "Equity", "₹8,76,540", "+22.3%", SuccessGreen),
        AssetDetail("EPF Account", "Retirement", "₹1,95,000", "+8.5%", OrangeGlow),
        AssetDetail("Gold Investment", "Commodities", "₹1,20,000", "+12.8%", Color(0xFFFFD700))
    )
}

@Composable
private fun AssetDetailCard(asset: AssetDetail) {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = asset.color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = asset.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = asset.value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = asset.growth,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = asset.color
                )
            }
        }
    }
}
