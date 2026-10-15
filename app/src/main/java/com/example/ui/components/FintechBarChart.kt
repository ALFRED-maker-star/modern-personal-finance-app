package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.PennyCoralExpense
import com.example.ui.theme.PennyGreenPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.CategorySpend
import com.example.ui.viewmodel.PeriodComparison
import java.text.NumberFormat
import java.util.Locale

@Composable
fun IncomeExpenseComparisonCard(
    comparisons: List<PeriodComparison>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val maxVal = comparisons.flatMap { listOf(it.income, it.expense) }.maxOrNull()?.coerceAtLeast(10.0) ?: 100.0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INFLOW VS OUTFLOW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )
                    )
                    Text(
                        text = "Weekly Comparisons",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Legend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PennyGreenPrimary))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("In", style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary))
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PennyCoralExpense))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Out", style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bars row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                comparisons.forEach { item ->
                    val incomeHeightRatio = (item.income / maxVal).toFloat().coerceIn(0.04f, 1f)
                    val expenseHeightRatio = (item.expense / maxVal).toFloat().coerceIn(0.04f, 1f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(130.dp)
                        ) {
                            // Income Bar
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .fillMaxHeight(incomeHeightRatio)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(PennyGreenPrimary)
                            )
                            // Expense Bar
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .fillMaxHeight(expenseHeightRatio)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(PennyCoralExpense)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SlateTextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryHorizontalBarRow(
    categorySpend: CategorySpend,
    currencySymbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val meta = CategoryRegistry.getCategory(categorySpend.category, isExpense = true)
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(categorySpend.percentage) {
        animProgress.animateTo(categorySpend.percentage, animationSpec = tween(600))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("category_bar_${categorySpend.category}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(meta.color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = meta.icon,
                        contentDescription = meta.name,
                        tint = meta.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categorySpend.category,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalTextPrimary
                        )
                    )
                    Text(
                        text = "${categorySpend.transactionCount} transactions",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextMuted)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$currencySymbol${formatter.format(categorySpend.totalAmount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalTextPrimary
                        )
                    )
                    Text(
                        text = "${(categorySpend.percentage * 100).toInt()}% of spend",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = meta.color
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rounded Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SlateSurfaceSubtle)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress.value.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(meta.color)
                )
            }
        }
    }
}
