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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryRegistry
import com.example.ui.components.IncomeExpenseComparisonCard
import com.example.ui.components.MonthlySpendingLineChartCard
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.PennyCoralContainer
import com.example.ui.theme.PennyCoralExpense
import com.example.ui.theme.PennyGreenContainer
import com.example.ui.theme.PennyGreenPrimary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MonthlyReportScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reportData by viewModel.monthlyReportData.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SlateSurface)
                        .testTag("monthly_report_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CharcoalTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Monthly Financial Report",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Executive analytics & spending velocity",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                    )
                }
            }
        }

        // Metrics Trio (Net Savings, Savings Rate, Daily Average)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Net Savings Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "NET SAVED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${settings.currencySymbol}${formatter.format(reportData.netSavings)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (reportData.netSavings >= 0) PennyGreenPrimary else PennyCoralExpense
                            )
                        )
                    }
                }

                // Savings Rate Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SAVINGS RATE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${reportData.savingsRate.toInt()}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PennyGreenPrimary
                            )
                        )
                    }
                }

                // Daily Average Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "DAILY AVG",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${settings.currencySymbol}${formatter.format(reportData.dailyAverage)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalTextPrimary
                            )
                        )
                    }
                }
            }
        }

        // Line Chart of Spending Over Month
        item {
            MonthlySpendingLineChartCard(
                dayPoints = reportData.dayPoints,
                currencySymbol = settings.currencySymbol
            )
        }

        // Inflow vs Outflow Comparison Bar Chart
        item {
            IncomeExpenseComparisonCard(
                comparisons = reportData.weekBars,
                currencySymbol = settings.currencySymbol
            )
        }

        // Top 5 Spending Categories Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "TOP 5 EXPENSE CATEGORIES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )
                    )
                    Text(
                        text = "Highest Outflow Drivers",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (reportData.topCategories.isEmpty()) {
                        Text(
                            text = "No category data available for this month.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            reportData.topCategories.forEachIndexed { index, catSpend ->
                                val meta = CategoryRegistry.getCategory(catSpend.category, isExpense = true)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = SlateSurfaceSubtle,
                                        shape = CircleShape,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "#${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(meta.color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = meta.icon,
                                            contentDescription = null,
                                            tint = meta.color,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = catSpend.category,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp
                                            )
                                        )
                                        Text(
                                            text = "${catSpend.transactionCount} transactions",
                                            style = MaterialTheme.typography.labelSmall.copy(color = SlateTextMuted)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${settings.currencySymbol}${formatter.format(catSpend.totalAmount)}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = CharcoalTextPrimary
                                            )
                                        )
                                        Text(
                                            text = "${(catSpend.percentage * 100).toInt()}% of total",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = meta.color
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
