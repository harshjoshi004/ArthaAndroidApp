package com.example.mcpclient.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcpclient.presentation.components.*
import com.example.mcpclient.presentation.viewmodel.McpViewModel
import com.example.mcpclient.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TransactionsScreen(
    viewModel: McpViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var animationTrigger by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        // Fetch transaction data when screen loads
        viewModel.fetchBankTransactions()
        viewModel.fetchMfTransactions()
        viewModel.fetchStockTransactions()
        
        // Trigger staggered animations
        repeat(10) {
            delay(100)
            animationTrigger++
        }
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TransactionHeader(onNavigateBack)
        }
        
        item {
            TransactionSummaryCards()
        }
        
        item {
            TransactionFilters(selectedFilter) { selectedFilter = it }
        }
        
        item {
            SpendingAnalytics()
        }
        
        val realTransactions = generateRealTransactions(viewModel)
        items(realTransactions) { transaction ->
            AnimatedTransactionItem(
                transaction = transaction,
                animationDelay = transaction.id * 100L
            )
        }
    }
}

@Composable
private fun TransactionHeader(onNavigateBack: () -> Unit) {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = ElectricBlue
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Track your financial activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.width(16.dp))
            
            GlowingButton(
                text = "Back",
                onClick = onNavigateBack,
                glowColor = ElectricBlue
            )
        }
    }
}

@Composable
private fun TransactionSummaryCards() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            listOf(
                Triple("Total Spent", "₹2,45,670", ErrorRed),
                Triple("Total Earned", "₹3,50,000", SuccessGreen),
                Triple("Net Flow", "+₹1,04,330", NeonGreen),
                Triple("Avg. Daily", "₹8,189", ElectricBlue)
            )
        ) { (title, amount, color) ->
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
                        text = amount,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )
                    
                    // Mini trend indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (color == SuccessGreen || color == NeonGreen) "↗" else "↘",
                            color = color,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${(10..25).random()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionFilters(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("All", "Income", "Expenses", "Investments", "Bills")
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter,
                        color = if (selectedFilter == filter) DarkBackground else TextSecondary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricBlue,
                    containerColor = DarkCard
                )
            )
        }
    }
}

@Composable
private fun SpendingAnalytics() {
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = PurpleGlow
    ) {
        Column {
            Text(
                text = "Spending Categories",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val spendingData = listOf(
                "Food & Dining" to 45000f,
                "Shopping" to 32000f,
                "Transportation" to 18000f,
                "Bills & Utilities" to 25000f,
                "Entertainment" to 12000f
            )
            
            AnimatedBarChart(
                data = spendingData,
                barColor = PurpleGlow,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AnimatedTransactionItem(
    transaction: TransactionItem,
    animationDelay: Long
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = animationDelay.toInt(),
            easing = FastOutSlowInEasing
        ),
        label = "transactionAlpha"
    )
    
    AnimatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = animatedAlpha
                translationX = (1f - animatedAlpha) * 300f
            },
        glowColor = transaction.color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    transaction.color.copy(alpha = 0.3f),
                                    transaction.color.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                
                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "${transaction.date} • ${transaction.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = transaction.amount,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = transaction.color
                )
                Text(
                    text = transaction.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
    }
}

data class TransactionItem(
    val id: Int,
    val description: String,
    val amount: String,
    val date: String,
    val category: String,
    val icon: String,
    val color: Color,
    val status: String
)

private fun generateRealTransactions(viewModel: McpViewModel): List<TransactionItem> {
    val bankTransactionsData = viewModel.bankTransactionsData
    val mfTransactionsData = viewModel.mfTransactionsData
    val stockTransactionsData = viewModel.stockTransactionsData
    
    val realTransactions = mutableListOf<TransactionItem>()
    var idCounter = 1
    
    // Add bank transactions
    bankTransactionsData?.bankTransactions?.forEach { bankTxn ->
        bankTxn.txns.take(10).forEach { txn ->
            if (txn.size >= 4) {
                val description = txn.getOrNull(1)?.toString()?.take(20) ?: "Bank Transaction"
                val amount = txn.getOrNull(2)?.toString()?.toDoubleOrNull() ?: 0.0
                val date = txn.getOrNull(0)?.toString()?.take(10) ?: "Recent"
                
                val category = when {
                    description.contains("salary", true) -> "Income"
                    description.contains("rent", true) -> "Bills"
                    description.contains("sip", true) || description.contains("mutual", true) -> "Investment"
                    description.contains("grocery", true) || description.contains("food", true) -> "Food"
                    description.contains("fuel", true) || description.contains("petrol", true) -> "Transport"
                    description.contains("electricity", true) || description.contains("bill", true) -> "Bills"
                    else -> "Expense"
                }
                
                val icon = when (category) {
                    "Income" -> "💰"
                    "Bills" -> "🏠"
                    "Investment" -> "📈"
                    "Food" -> "🛒"
                    "Transport" -> "⛽"
                    "Entertainment" -> "🎬"
                    "Shopping" -> "📦"
                    else -> "💳"
                }
                
                val color = if (amount >= 0) SuccessGreen else when (category) {
                    "Bills" -> ErrorRed
                    "Investment" -> ElectricBlue
                    "Food" -> OrangeGlow
                    "Transport" -> PurpleGlow
                    "Entertainment" -> PinkGlow
                    "Shopping" -> NeonGreen
                    else -> TextSecondary
                }
                
                realTransactions.add(
                    TransactionItem(
                        id = idCounter++,
                        description = description,
                        amount = if (amount >= 0) "+₹${amount.toInt()}" else "-₹${(-amount).toInt()}",
                        date = date,
                        category = category,
                        icon = icon,
                        color = color,
                        status = "Completed"
                    )
                )
            }
        }
    }
    
    // Add MF transactions
    mfTransactionsData?.mfTransactions?.forEach { mfTxn ->
        mfTxn.txns.take(5).forEach { txn ->
            if (txn.size >= 3) {
                val schemeName = mfTxn.schemeName.take(20)
                val amount = txn.getOrNull(2)?.toString()?.toDoubleOrNull() ?: 0.0
                val date = txn.getOrNull(0)?.toString()?.take(10) ?: "Recent"
                
                realTransactions.add(
                    TransactionItem(
                        id = idCounter++,
                        description = "MF: $schemeName",
                        amount = if (amount >= 0) "+₹${amount.toInt()}" else "-₹${(-amount).toInt()}",
                        date = date,
                        category = "Investment",
                        icon = "📊",
                        color = ElectricBlue,
                        status = "Completed"
                    )
                )
            }
        }
    }
    
    return if (realTransactions.isNotEmpty()) {
        realTransactions.sortedByDescending { it.id }.take(15)
    } else {
        generateSampleTransactions()
    }
}

private fun generateSampleTransactions(): List<TransactionItem> {
    return listOf(
        TransactionItem(1, "Salary Credit", "+₹3,00,000", "Dec 28", "Income", "💰", SuccessGreen, "Completed"),
        TransactionItem(2, "Rent Payment", "-₹25,000", "Dec 27", "Bills", "🏠", ErrorRed, "Completed"),
        TransactionItem(3, "SIP Investment", "-₹30,000", "Dec 26", "Investment", "📈", ElectricBlue, "Completed"),
        TransactionItem(4, "Grocery Shopping", "-₹3,500", "Dec 25", "Food", "🛒", OrangeGlow, "Completed"),
        TransactionItem(5, "Fuel", "-₹2,800", "Dec 24", "Transport", "⛽", PurpleGlow, "Completed"),
        TransactionItem(6, "Movie Tickets", "-₹800", "Dec 23", "Entertainment", "🎬", PinkGlow, "Completed"),
        TransactionItem(7, "Online Shopping", "-₹5,600", "Dec 22", "Shopping", "📦", NeonGreen, "Completed"),
        TransactionItem(8, "Restaurant", "-₹1,200", "Dec 21", "Food", "🍽️", ErrorRed, "Completed"),
        TransactionItem(9, "Electricity Bill", "-₹1,800", "Dec 20", "Bills", "⚡", WarningOrange, "Completed"),
        TransactionItem(10, "Freelance Payment", "+₹15,000", "Dec 19", "Income", "💻", SuccessGreen, "Completed")
    )
}
