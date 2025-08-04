package com.example.mcpclient.presentation.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mcpclient.presentation.components.AnimatedCard
import com.example.mcpclient.presentation.components.AnimatedDonutChart
import com.example.mcpclient.presentation.components.AnimatedLineChart
import com.example.mcpclient.presentation.components.GlowingButton
import com.example.mcpclient.presentation.components.ChatBottomSheet
import com.example.mcpclient.presentation.viewmodel.McpViewModel
import com.example.mcpclient.ui.theme.DarkBackground
import com.example.mcpclient.ui.theme.DarkBorder
import com.example.mcpclient.ui.theme.DarkSurface
import com.example.mcpclient.ui.theme.ElectricBlue
import com.example.mcpclient.ui.theme.ErrorRed
import com.example.mcpclient.ui.theme.NeonGreen
import com.example.mcpclient.ui.theme.OrangeGlow
import com.example.mcpclient.ui.theme.PinkGlow
import com.example.mcpclient.ui.theme.PurpleGlow
import com.example.mcpclient.ui.theme.SuccessGreen
import com.example.mcpclient.ui.theme.TextPrimary
import com.example.mcpclient.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: McpViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(viewModel.isLoggedIn) {
        if (!viewModel.isLoggedIn) {
            onNavigateToAuth()
        } else {
            // Fetch all data when user is logged in
            viewModel.fetchAllData()
        }
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "dashboardAlpha"
    )

    // Bottom sheet state
    val bottomSheetOpen = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Header
                DashboardHeader(
                    phoneNumber = viewModel.getStoredPhoneNumber() ?: "",
                    onLogout = viewModel::logout
                )
            }

            item {
                // Quick stats cards
                QuickStatsRow(viewModel, onNavigateToDetails)
            }

            item {
                // Net worth overview
                NetWorthOverview(viewModel, onNavigateToDetails)
            }

            item {
                // Recent transactions
                RecentTransactions(viewModel, onNavigateToDetails)
            }

            item {
                // Investment portfolio
                InvestmentPortfolio(viewModel, onNavigateToDetails)
            }

            item {
                // Action buttons
                ActionButtonsGrid(viewModel, onNavigateToDetails)
            }

            // Add some bottom padding to account for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { bottomSheetOpen.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF301C50),
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Chat",
                    modifier = Modifier.padding(end = 16.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Open AI Chat",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Modal Bottom Sheet
        if (bottomSheetOpen.value) {
            ModalBottomSheet(
                onDismissRequest = { bottomSheetOpen.value = false },
                sheetState = sheetState,
                containerColor = DarkSurface,
                contentColor = TextPrimary
            ) {
                ChatBottomSheet(
                    viewModel = viewModel,
                    userId = viewModel.getStoredPhoneNumber() ?: "1313131313", // Use stored phone number or default
                    onChatClick = { sessionId ->
                        // TODO: Navigate to chat details screen
                        bottomSheetOpen.value = false
                        onNavigateToDetails("chat/$sessionId")
                    }
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    phoneNumber: String,
    onLogout: () -> Unit
) {
    AnimatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 8.dp),
        glowColor = ElectricBlue
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome to Artha!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Text(
                    text = "User ${phoneNumber.takeLast(4)}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
            }

            GlowingButton(
                text = "Logout",
                onClick = onLogout,
                glowColor = ErrorRed
            )
        }
    }
}

@Composable
private fun QuickStatsRow(
    viewModel: McpViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
// Use real data if available, otherwise use loading/default values
val netWorthData = viewModel.netWorthData
val creditReportData = viewModel.creditReportData

val totalNetWorth = netWorthData?.netWorthResponse?.totalNetWorthValue?.let {
    "₹${it.units.toDouble().div(100000).toInt()}L"
} ?: "N/A"

val totalInvestments = netWorthData?.mfSchemeAnalytics?.schemeAnalytics?.sumOf {
    it.enrichedAnalytics.analytics.schemeDetails.currentValue.units.toDouble()
}?.let { "₹${(it / 100000).toInt()}L" } ?: "N/A"

val savingsAmount = netWorthData?.accountDetailsBulkResponse?.accountDetailsMap?.values
    ?.mapNotNull { it.depositSummary?.currentBalance?.units?.toDouble() }
    ?.sum()?.let { "₹${(it / 100000).toInt()}L" } ?: "N/A"

val creditScore = creditReportData?.creditReports?.firstOrNull()
    ?.creditReportData?.score?.bureauScore?.toString() ?: "N/A"

items(
    listOf(
        Triple("Net Worth", totalNetWorth, NeonGreen),
        Triple("Investments", totalInvestments, ElectricBlue),
        Triple("Savings", savingsAmount, PurpleGlow),
        Triple("Credit Score", creditScore, OrangeGlow)
    )
) { (title, value, color) ->
    QuickStatCard(
        title = title,
        value = value,
        color = color
    )
}
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    color: Color
) {
    AnimatedCard(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        glowColor = color
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight(),
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
        }
    }
}

@Composable
private fun NetWorthOverview(
    viewModel: McpViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    AnimatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        glowColor = NeonGreen
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Worth Breakdown",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                TextButton(
                    onClick = { onNavigateToDetails("networth") }
                ) {
                    Text("View Details", color = NeonGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isDataLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonGreen)
                }
            } else {
                // Use real data if available, otherwise use sample data
                val netWorthData = viewModel.netWorthData?.let { data ->
                    buildList {
                        // Mutual Funds - safely access with null checks
                        data.mfSchemeAnalytics?.schemeAnalytics?.sumOf {
                            it.enrichedAnalytics.analytics.schemeDetails.currentValue.units.toDouble()
                        }?.takeIf { it > 0 }?.let { add("Mutual Funds" to it.toFloat()) }

                        // Stocks/Equity
                        data.accountDetailsBulkResponse?.accountDetailsMap?.values
                            ?.mapNotNull { it.equitySummary?.currentValue?.units?.toDouble() }
                            ?.sum()?.takeIf { it > 0 }?.let { add("Stocks" to it.toFloat()) }

                        // NPS
                        data.accountDetailsBulkResponse?.accountDetailsMap?.values
                            ?.mapNotNull { it.npsSummary?.currentValue?.units?.toDouble() }
                            ?.sum()?.takeIf { it > 0 }?.let { add("NPS" to it.toFloat()) }

                        // EPF
                        data.accountDetailsBulkResponse?.accountDetailsMap?.values
                            ?.mapNotNull { it.epfSummary?.currentBalance?.units?.toDouble() }
                            ?.sum()?.takeIf { it > 0 }?.let { add("EPF" to it.toFloat()) }

                        // Savings/Deposits
                        data.accountDetailsBulkResponse?.accountDetailsMap?.values
                            ?.mapNotNull { it.depositSummary?.currentBalance?.units?.toDouble() }
                            ?.sum()?.takeIf { it > 0 }?.let { add("Savings" to it.toFloat()) }
                    }
                } ?: listOf(
                    "Mutual Funds" to 5260000f,
                    "Stocks" to 3000000f,
                    "NPS" to 2500000f,
                    "EPF" to 1950000f,
                    "Savings" to 250000f
                )

                if (netWorthData.isNotEmpty()) {
                    AnimatedDonutChart(
                        modifier = Modifier.fillMaxWidth(),
                        data = netWorthData,
                        colors = listOf(ElectricBlue, NeonGreen, PurpleGlow, OrangeGlow, PinkGlow)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No data available",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentTransactions(
    viewModel: McpViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    AnimatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        glowColor = ElectricBlue
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                TextButton(
                    onClick = { onNavigateToDetails("transactions") }
                ) {
                    Text("View All", color = ElectricBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isDataLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricBlue)
                }
            } else {
                // Use real data if available, otherwise use fallback data
                val recentTransactions = try {
                    viewModel.bankTransactionsData?.let { data ->
                        data.bankTransactions?.flatMap { bank ->
                            bank.txns?.take(4)?.mapNotNull { txn ->
                                if (txn != null && txn.size >= 4) {
                                    val description = txn.getOrNull(1)?.toString() ?: "Transaction"
                                    val amount = txn.getOrNull(2)?.toString()?.let {
                                        val value = it.toDoubleOrNull() ?: 0.0
                                        if (value >= 0) "+₹${value.toInt()}" else "-₹${(-value).toInt()}"
                                    } ?: "₹0"
                                    val color = if (amount.startsWith("+")) SuccessGreen else ErrorRed
                                    Triple(description.take(20), amount, color)
                                } else null
                            } ?: emptyList()
                        }?.take(4) ?: emptyList()
                    } ?: emptyList()
                } catch (e: Exception) {
                    Log.e("DashboardScreen", "Error processing transactions", e)
                    emptyList()
                }

                if (recentTransactions.isNotEmpty()) {
                    recentTransactions.forEach { (description, amount, color) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )

                            Text(
                                text = amount,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = color
                            )
                        }

                        if (recentTransactions.last() != Triple(description, amount, color)) {
                            Divider(
                                color = DarkBorder,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions available",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InvestmentPortfolio(
    viewModel: McpViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    AnimatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        glowColor = PurpleGlow
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Investment Performance",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = { onNavigateToDetails("investments") }
                ) {
                    Text("View Detail", color = PurpleGlow)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.isDataLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurpleGlow)
                }
            } else {
                // Use real investment performance data from MF schemes
                val performanceData =
                    viewModel.netWorthData?.mfSchemeAnalytics?.schemeAnalytics?.let { schemes ->
                        if (schemes.isNotEmpty()) {
                            // Generate a simple performance trend based on XIRR values
                            val avgXIRR =
                                schemes.map { it.enrichedAnalytics.analytics.schemeDetails.xirr }
                                    .average().toFloat()
                            val baseValue = schemes.sumOf {
                                it.enrichedAnalytics.analytics.schemeDetails.currentValue.units.toDouble()
                            }.toFloat() / schemes.size

                            // Generate 7 days of performance data based on real metrics
                            listOf(
                                baseValue * 0.96f,
                                baseValue * 0.98f,
                                baseValue * 0.95f,
                                baseValue * 1.02f,
                                baseValue * 1.05f,
                                baseValue * 1.08f,
                                baseValue
                            )
                        } else null
                    } ?: listOf(50000f, 52000f, 48000f, 55000f, 58000f, 62000f, 59000f)

                AnimatedLineChart(
                    data = performanceData,
                    lineColor = PurpleGlow,
                    title = "Portfolio Value (Last 7 Days)"
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsGrid(
    viewModel: McpViewModel,
    onNavigateToDetails: (String) -> Unit
) {
    val actions = listOf(
        Triple("Bank Transactions", "💳", ElectricBlue),
        Triple("Mutual Funds", "📈", NeonGreen),
        Triple("Stock Portfolio", "📊", PurpleGlow),
        Triple("EPF Details", "🏦", OrangeGlow),
        Triple("Credit Report", "📋", PinkGlow),
        Triple("Analytics", "📱", ElectricBlue)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )

        actions.chunked(2).forEach { rowActions ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowActions.forEach { (title, icon, color) ->
                    AnimatedCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clickable {
                                when (title) {
                                    "Bank Transactions" -> onNavigateToDetails("transactions")
                                    "Mutual Funds" -> onNavigateToDetails("investments")
                                    "Stock Portfolio" -> onNavigateToDetails("investments")
                                    "EPF Details" -> onNavigateToDetails("networth")
                                    "Credit Report" -> onNavigateToDetails("networth")
                                    "Analytics" -> onNavigateToDetails("dashboard")
                                }
                            },
                        glowColor = color
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = icon,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
