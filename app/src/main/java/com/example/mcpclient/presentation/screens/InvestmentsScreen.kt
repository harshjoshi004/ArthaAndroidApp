package com.example.mcpclient.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcpclient.presentation.components.*
import com.example.mcpclient.presentation.viewmodel.McpViewModel
import com.example.mcpclient.ui.theme.*
import kotlin.math.*

@Composable
fun InvestmentsScreen(
    viewModel: McpViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fetch investment data when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchNetWorth()
        viewModel.fetchMfTransactions()
        viewModel.fetchStockTransactions()
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
            InvestmentHeader(onNavigateBack)
        }
        
        item {
            PortfolioOverview(viewModel)
        }
        
        item {
            PerformanceChart()
        }
        
        item {
            AssetAllocation()
        }
        
        item {
            TopPerformers(viewModel)
        }
        
        val realInvestments = generateRealInvestments(viewModel)
        items(realInvestments) { investment ->
            InvestmentCard(investment)
        }
    }
}

@Composable
private fun InvestmentHeader(onNavigateBack: () -> Unit) {
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
                    text = "Investment Portfolio",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Track your wealth growth",
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
private fun PortfolioOverview(viewModel: McpViewModel) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (viewModel.isDataLoading) {
            items(4) {
                AnimatedCard(
                    modifier = Modifier
                        .width(160.dp)
                        .height(120.dp),
                    glowColor = ElectricBlue
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ElectricBlue)
                    }
                }
            }
        } else {
            val netWorthData = viewModel.netWorthData
            val mfSchemes = netWorthData?.mfSchemeAnalytics?.schemeAnalytics ?: emptyList()
            
            val totalCurrentValue = mfSchemes.sumOf {
                it.enrichedAnalytics.analytics.schemeDetails.currentValue.units.toDouble()
            }
            val totalInvestedValue = mfSchemes.sumOf {
                it.enrichedAnalytics.analytics.schemeDetails.investedValue.units.toDouble()
            }
            val totalGains = totalCurrentValue - totalInvestedValue
            val avgXIRR = if (mfSchemes.isNotEmpty()) {
                mfSchemes.map { it.enrichedAnalytics.analytics.schemeDetails.xirr }.average()
            } else 18.5
            
            val portfolioData = listOf(
                Triple("Total Value", "₹${(totalCurrentValue / 100000).toInt()}.${((totalCurrentValue % 100000) / 10000).toInt()}L", NeonGreen),
                Triple("Total Invested", "₹${(totalInvestedValue / 100000).toInt()}.${((totalInvestedValue % 100000) / 10000).toInt()}L", ElectricBlue),
                Triple("Total Gains", "₹${(totalGains / 100000).toInt()}.${((totalGains % 100000) / 10000).toInt()}L", SuccessGreen),
                Triple("XIRR", "${avgXIRR.toInt()}.${((avgXIRR % 1) * 10).toInt()}%", PurpleGlow)
            ).takeIf { mfSchemes.isNotEmpty() } ?: listOf(
                Triple("Total Value", "₹8,76,540", NeonGreen),
                Triple("Total Invested", "₹6,50,000", ElectricBlue),
                Triple("Total Gains", "₹2,26,540", SuccessGreen),
                Triple("XIRR", "18.5%", PurpleGlow)
            )
            
            items(portfolioData) { (title, value, color) ->
                AnimatedCard(
                    modifier = Modifier
                        .width(160.dp)
                        .height(120.dp),
                    glowColor = color
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        
                        Text(
                            text = value,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = color
                        )
                        
                        // Animated progress indicator
                        AnimatedProgressIndicator(color)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedProgressIndicator(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress")
    
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progressAnimation"
    )
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        drawRoundRect(
            color = color.copy(alpha = 0.2f),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )
        
        drawRoundRect(
            color = color,
            size = size.copy(width = size.width * progress),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )
    }
}

@Composable
private fun PerformanceChart() {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = ElectricBlue
    ) {
        Column {
            Text(
                text = "Portfolio Performance",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Multi-line chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                drawMultiLineChart()
            }
            
            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem("Portfolio", ElectricBlue)
                LegendItem("Nifty 50", NeonGreen)
                LegendItem("Sensex", PurpleGlow)
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

private fun DrawScope.drawMultiLineChart() {
    val portfolioData = listOf(100f, 105f, 98f, 112f, 118f, 125f, 135f, 142f, 138f, 145f)
    val niftyData = listOf(100f, 102f, 99f, 108f, 112f, 115f, 120f, 125f, 122f, 128f)
    val sensexData = listOf(100f, 103f, 101f, 110f, 114f, 118f, 122f, 127f, 124f, 130f)
    
    drawAnimatedMultiLine(portfolioData, ElectricBlue)
    drawAnimatedMultiLine(niftyData, NeonGreen)
    drawAnimatedMultiLine(sensexData, PurpleGlow)
}

private fun DrawScope.drawAnimatedMultiLine(data: List<Float>, color: Color) {
    if (data.isEmpty()) return
    
    val maxValue = 150f
    val minValue = 90f
    val range = maxValue - minValue
    
    val stepX = size.width / (data.size - 1)
    val points = data.mapIndexed { index, value ->
        val x = index * stepX
        val y = size.height - ((value - minValue) / range) * size.height
        Offset(x, y)
    }
    
    // Draw line
    for (i in 0 until points.size - 1) {
        drawLine(
            color = color,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 3.dp.toPx()
        )
    }
    
    // Draw points
    points.forEach { point ->
        drawCircle(
            color = color,
            radius = 4.dp.toPx(),
            center = point
        )
    }
}

@Composable
private fun AssetAllocation() {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = PurpleGlow
    ) {
        Column {
            Text(
                text = "Asset Allocation",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val allocationData = listOf(
                "Equity Mutual Funds" to 450000f,
                "Debt Funds" to 180000f,
                "Direct Stocks" to 200000f,
                "Gold ETF" to 46540f
            )
            
            AnimatedDonutChart(
                data = allocationData,
                colors = listOf(ElectricBlue, NeonGreen, PurpleGlow, OrangeGlow),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TopPerformers(viewModel: McpViewModel) {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = SuccessGreen
    ) {
        Column {
            Text(
                text = "Top Performers",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (viewModel.isDataLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SuccessGreen)
                }
            } else {
                val performers = viewModel.netWorthData?.mfSchemeAnalytics?.schemeAnalytics?.let { schemes ->
                    schemes.sortedByDescending { it.enrichedAnalytics.analytics.schemeDetails.xirr }
                        .take(3)
                        .mapIndexed { index, scheme ->
                            val colors = listOf(SuccessGreen, NeonGreen, ElectricBlue)
                            val returns = scheme.enrichedAnalytics.analytics.schemeDetails.xirr
                            val name = scheme.schemeDetail.nameData.longName.take(25)
                            Triple(
                                name,
                                if (returns >= 0) "+${returns.toInt()}.${((returns % 1) * 10).toInt()}%" else "${returns.toInt()}.${((returns % 1) * 10).toInt()}%",
                                colors[index]
                            )
                        }
                } ?: listOf(
                    Triple("HDFC Small Cap Fund", "+28.5%", SuccessGreen),
                    Triple("Axis Bluechip Fund", "+22.1%", NeonGreen),
                    Triple("SBI Technology Fund", "+35.8%", ElectricBlue)
                )
                
                performers.forEach { (name, returns, color) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        
                        Text(
                            text = returns,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InvestmentCard(investment: InvestmentItem) {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = investment.color
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = investment.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = investment.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = investment.currentValue,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = investment.returns,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = investment.color
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Invested: ${investment.invested}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                Text(
                    text = "Units: ${investment.units}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
    }
}

data class InvestmentItem(
    val name: String,
    val category: String,
    val currentValue: String,
    val invested: String,
    val returns: String,
    val units: String,
    val color: Color
)

private fun generateRealInvestments(viewModel: McpViewModel): List<InvestmentItem> {
    val netWorthData = viewModel.netWorthData
    val mfSchemes = netWorthData?.mfSchemeAnalytics?.schemeAnalytics ?: emptyList()
    
    return if (mfSchemes.isNotEmpty()) {
        mfSchemes.mapIndexed { index, scheme ->
            val schemeDetails = scheme.enrichedAnalytics.analytics.schemeDetails
            val currentValue = schemeDetails.currentValue.units.toDouble()
            val investedValue = schemeDetails.investedValue.units.toDouble()
            val returns = if (investedValue > 0) ((currentValue - investedValue) / investedValue) * 100 else 0.0
            val xirr = scheme.enrichedAnalytics.analytics.schemeDetails.xirr
            
            val colors = listOf(SuccessGreen, NeonGreen, ElectricBlue, PurpleGlow, OrangeGlow, PinkGlow)
            val color = colors[index % colors.size]
            
            val fundName = scheme.schemeDetail.nameData.longName.take(25)
            val category = when {
                scheme.schemeDetail.categoryName.contains("Large", true) -> "Large Cap"
                scheme.schemeDetail.categoryName.contains("Mid", true) -> "Mid Cap"
                scheme.schemeDetail.categoryName.contains("Small", true) -> "Small Cap"
                scheme.schemeDetail.categoryName.contains("Debt", true) -> "Debt"
                scheme.schemeDetail.categoryName.contains("Hybrid", true) -> "Hybrid"
                else -> scheme.schemeDetail.categoryName.take(15)
            }
            
            InvestmentItem(
                name = fundName,
                category = category,
                currentValue = "₹${(currentValue / 100000).toInt()},${((currentValue % 100000) / 1000).toInt().toString().padStart(2, '0')}",
                invested = "₹${(investedValue / 100000).toInt()},${((investedValue % 100000) / 1000).toInt().toString().padStart(2, '0')}",
                returns = if (returns >= 0) "+${returns.toInt()}.${((returns % 1) * 10).toInt()}%" else "${returns.toInt()}.${((returns % 1) * 10).toInt()}%",
                units = schemeDetails.units.toString(),
                color = color
            )
        }
    } else {
        // Fallback to sample data if no real data available
        generateSampleInvestments()
    }
}

private fun generateSampleInvestments(): List<InvestmentItem> {
    return listOf(
        InvestmentItem("HDFC Top 100 Fund", "Large Cap", "₹1,25,680", "₹1,00,000", "+25.68%", "2,456", SuccessGreen),
        InvestmentItem("Axis Small Cap Fund", "Small Cap", "₹85,420", "₹60,000", "+42.37%", "1,234", NeonGreen),
        InvestmentItem("SBI Blue Chip Fund", "Large Cap", "₹2,15,890", "₹1,80,000", "+19.94%", "4,567", ElectricBlue),
        InvestmentItem("Mirae Asset Emerging", "Mid Cap", "₹95,670", "₹75,000", "+27.56%", "1,890", PurpleGlow),
        InvestmentItem("ICICI Prudential Tech", "Sectoral", "₹1,45,230", "₹1,20,000", "+21.03%", "2,345", OrangeGlow)
    )
}
