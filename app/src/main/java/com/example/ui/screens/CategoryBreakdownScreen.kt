package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PieChart
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
import com.example.ui.components.CategoryHorizontalBarRow
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.PennyCoralExpense
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
fun CategoryBreakdownScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.thisMonthStats.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val selectedDrillCategory by viewModel.selectedDrillCategory.collectAsStateWithLifecycle()

    val drillCategoryTransactions = if (selectedDrillCategory != null) {
        allTransactions.filter { it.category.equals(selectedDrillCategory, ignoreCase = true) }
    } else {
        emptyList()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SlateSurface)
                        .testTag("category_breakdown_back_button")
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
                        text = "Category Breakdown",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Detailed outflow by classification",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                    )
                }
            }

            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL EXPENSE SPENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.formatAmount(stats.periodExpense),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PennyCoralExpense
                            )
                        )
                    }

                    Surface(
                        color = SlateSurfaceSubtle,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${stats.categorySpends.size} Active Categories",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = CharcoalTextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // If a category is tapped to drill down, show dedicated banner and transactions
            AnimatedVisibility(visible = selectedDrillCategory != null) {
                val catName = selectedDrillCategory ?: ""
                val meta = CategoryRegistry.getCategory(catName)
                val catSum = drillCategoryTransactions.sumOf { it.amount }
                val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
                    minimumFractionDigits = 2
                    maximumFractionDigits = 2
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = meta.color.copy(alpha = 0.12f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(meta.color.copy(alpha = 0.4f)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(meta.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = meta.icon,
                                    contentDescription = null,
                                    tint = SlateSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$catName History",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${drillCategoryTransactions.size} transactions • ${settings.currencySymbol}${formatter.format(catSum)}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.setSelectedDrillCategory(null) },
                            modifier = Modifier.testTag("close_drill_down_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close drill down",
                                tint = CharcoalTextPrimary
                            )
                        }
                    }
                }
            }

            // Main List: either drill down transactions or horizontal bars list
            if (selectedDrillCategory != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(drillCategoryTransactions, key = { it.id }) { tx ->
                        TransactionItemCard(
                            transaction = tx,
                            formattedAmount = viewModel.formatAmount(tx.amount, withSign = true, isIncome = tx.isIncome),
                            onDelete = { viewModel.deleteTransaction(tx.id) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Tap any category to view its full transaction history",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                        )
                    }

                    items(stats.categorySpends, key = { it.category }) { spend ->
                        CategoryHorizontalBarRow(
                            categorySpend = spend,
                            currencySymbol = settings.currencySymbol,
                            onClick = {
                                viewModel.setSelectedDrillCategory(spend.category)
                            }
                        )
                    }
                }
            }
        }
    }
}
