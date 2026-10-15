package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BalanceHeroCard
import com.example.ui.components.IncomeExpenseSummaryRow
import com.example.ui.components.SpendingDonutChartCard
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.PennyGreenPrimary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.PeriodFilter

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToCategoryBreakdown: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.thisMonthStats.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val period by viewModel.dashboardPeriod.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PennyGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "PennyWise",
                                tint = SlateSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PennyWise",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                            Text(
                                text = "Smart Personal Finance",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                    }

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SlateSurface)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SlateTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Hero Balance Card
            item {
                BalanceHeroCard(
                    balance = stats.totalBalance,
                    currencySymbol = settings.currencySymbol,
                    formattedBalance = viewModel.formatAmount(stats.totalBalance)
                )
            }

            // Period Selector Toggle
            item {
                Surface(
                    color = SlateSurfaceSubtle,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PeriodFilter.entries.forEach { p ->
                            val isSelected = period == p
                            val label = when (p) {
                                PeriodFilter.THIS_MONTH -> "This Month"
                                PeriodFilter.THIS_WEEK -> "This Week"
                                PeriodFilter.ALL_TIME -> "All Time"
                            }
                            Surface(
                                selected = isSelected,
                                onClick = { viewModel.setDashboardPeriod(p) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) SlateSurface else SlateSurfaceSubtle,
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("period_tab_${p.name}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else SlateTextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Income vs Expense Summary Cards
            item {
                IncomeExpenseSummaryRow(
                    incomeText = viewModel.formatAmount(stats.periodIncome, withSign = true, isIncome = true),
                    expenseText = viewModel.formatAmount(stats.periodExpense, withSign = true, isIncome = false)
                )
            }

            // Spending Donut Chart
            item {
                SpendingDonutChartCard(
                    categorySpends = stats.categorySpends,
                    totalExpense = stats.periodExpense,
                    currencySymbol = settings.currencySymbol,
                    onCategoryClick = { category ->
                        viewModel.setSelectedDrillCategory(category)
                        onNavigateToCategoryBreakdown()
                    }
                )
            }

            // Recent Transactions Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RECENT TRANSACTIONS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                        )
                        Text(
                            text = "Latest Activity",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    TextButton(
                        onClick = onNavigateToTransactions,
                        modifier = Modifier.testTag("view_all_transactions_button")
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PennyGreenPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = PennyGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Recent transactions items
            if (stats.recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recent transactions yet",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                        )
                    }
                }
            } else {
                items(stats.recentTransactions, key = { it.id }) { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        formattedAmount = viewModel.formatAmount(tx.amount, withSign = true, isIncome = tx.isIncome),
                        onDelete = { viewModel.deleteTransaction(tx.id) }
                    )
                }
            }
        }

        // Floating Action Button to Add Transaction
        FloatingActionButton(
            onClick = onNavigateToAddTransaction,
            containerColor = PennyGreenPrimary,
            contentColor = SlateSurface,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(60.dp)
                .testTag("add_transaction_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Transaction",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
