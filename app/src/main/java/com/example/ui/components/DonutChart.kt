package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.CategorySpend
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpendingDonutChartCard(
    categorySpends: List<CategorySpend>,
    totalExpense: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    onCategoryClick: ((String) -> Unit)? = null
) {
    var selectedCategory by remember { mutableStateOf<CategorySpend?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(categorySpends) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spending_donut_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SPENDING BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )
                    )
                    Text(
                        text = "By Category This Month",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                if (selectedCategory != null) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = null }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (categorySpends.isEmpty() || totalExpense <= 0.0) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No expenses recorded this month",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + to add your first transaction",
                            style = MaterialTheme.typography.labelMedium.copy(color = SlateTextSecondary)
                        )
                    }
                }
            } else {
                // Donut Chart with Center Text
                Box(
                    modifier = Modifier
                        .size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(190.dp)) {
                        val strokeWidth = 24.dp.toPx()
                        val padding = strokeWidth / 2f
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                        val topLeft = Offset(padding, padding)

                        // Background track
                        drawArc(
                            color = SlateSurfaceSubtle,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )

                        var startAngle = -90f
                        categorySpends.forEach { spend ->
                            val sweepAngle = (spend.percentage * 360f * animationProgress.value).coerceAtLeast(0f)
                            if (sweepAngle > 0.5f) {
                                val categoryMeta = CategoryRegistry.getCategory(spend.category, isExpense = true)
                                val isSelected = selectedCategory?.category == spend.category
                                val activeStroke = if (isSelected) strokeWidth * 1.25f else strokeWidth
                                val sliceColor = if (selectedCategory == null || isSelected) {
                                    categoryMeta.color
                                } else {
                                    categoryMeta.color.copy(alpha = 0.35f)
                                }

                                drawArc(
                                    color = sliceColor,
                                    startAngle = startAngle,
                                    sweepAngle = (sweepAngle - 2f).coerceAtLeast(1f), // gap
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = activeStroke, cap = StrokeCap.Round)
                                )
                            }
                            startAngle += spend.percentage * 360f * animationProgress.value
                        }
                    }

                    // Center Details
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        val displaySpend = selectedCategory
                        if (displaySpend != null) {
                            Text(
                                text = displaySpend.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateTextSecondary,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                            val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
                                minimumFractionDigits = 2
                                maximumFractionDigits = 2
                            }
                            Text(
                                text = "$currencySymbol${formatter.format(displaySpend.totalAmount)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalTextPrimary
                                )
                            )
                            Text(
                                text = "${(displaySpend.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = CategoryRegistry.getCategory(displaySpend.category).color,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        } else {
                            Text(
                                text = "TOTAL SPENT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateTextMuted,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.8.sp
                                )
                            )
                            val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
                                minimumFractionDigits = 2
                                maximumFractionDigits = 2
                            }
                            Text(
                                text = "$currencySymbol${formatter.format(totalExpense)}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CharcoalTextPrimary
                                )
                            )
                            Text(
                                text = "${categorySpends.size} categories",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Legend Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorySpends.take(6).forEach { spend ->
                        val meta = CategoryRegistry.getCategory(spend.category, isExpense = true)
                        val isSelected = selectedCategory?.category == spend.category
                        val chipBg = if (isSelected) meta.color.copy(alpha = 0.2f) else SlateSurfaceSubtle

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(chipBg)
                                .clickable {
                                    selectedCategory = if (selectedCategory?.category == spend.category) null else spend
                                    onCategoryClick?.invoke(spend.category)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(meta.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = spend.category,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = CharcoalTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${(spend.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SlateTextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
