package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.PennyCoralContainer
import com.example.ui.theme.PennyCoralExpense
import com.example.ui.theme.PennyCoralText
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.DaySpend
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MonthlySpendingLineChartCard(
    dayPoints: List<DaySpend>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(dayPoints) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(700))
    }

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
                        text = "SPENDING TREND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )
                    )
                    Text(
                        text = "Daily Outflow Curve",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                val activePoint = selectedIndex?.let { dayPoints.getOrNull(it) } ?: dayPoints.lastOrNull()
                if (activePoint != null) {
                    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                    }
                    Surface(
                        color = PennyCoralContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Day ${activePoint.dayOfMonth}: $currencySymbol${formatter.format(activePoint.amount)}",
                            color = PennyCoralText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (dayPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No spending points available",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                    )
                }
            } else {
                val maxAmount = (dayPoints.maxOfOrNull { it.amount } ?: 1.0).coerceAtLeast(10.0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .pointerInput(dayPoints) {
                            detectTapGestures { offset ->
                                val step = size.width / (dayPoints.size - 1).coerceAtLeast(1)
                                val index = (offset.x / step).toInt().coerceIn(0, dayPoints.size - 1)
                                selectedIndex = index
                            }
                        }
                        .pointerInput(dayPoints) {
                            detectHorizontalDragGestures { change, _ ->
                                val step = size.width / (dayPoints.size - 1).coerceAtLeast(1)
                                val index = (change.position.x / step).toInt().coerceIn(0, dayPoints.size - 1)
                                selectedIndex = index
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val width = size.width
                        val height = size.height - 24.dp.toPx() // leave bottom room for axis
                        val stepX = width / (dayPoints.size - 1).coerceAtLeast(1)

                        // Draw horizontal subtle grid lines
                        val gridLines = 3
                        for (i in 1..gridLines) {
                            val y = height * (i.toFloat() / (gridLines + 1))
                            drawLine(
                                color = SlateBorder.copy(alpha = 0.8f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )
                        }

                        // Calculate points
                        val points = dayPoints.mapIndexed { index, item ->
                            val x = index * stepX
                            val normalizedY = (item.amount / maxAmount).toFloat().coerceIn(0f, 1f)
                            val y = height - (normalizedY * height * animationProgress.value)
                            Offset(x, y)
                        }

                        // Build smooth bezier path
                        val strokePath = Path()
                        val fillPath = Path()

                        if (points.isNotEmpty()) {
                            strokePath.moveTo(points.first().x, points.first().y)
                            fillPath.moveTo(points.first().x, height)
                            fillPath.lineTo(points.first().x, points.first().y)

                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                                val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)

                                strokePath.cubicTo(
                                    controlPoint1.x, controlPoint1.y,
                                    controlPoint2.x, controlPoint2.y,
                                    p1.x, p1.y
                                )
                                fillPath.cubicTo(
                                    controlPoint1.x, controlPoint1.y,
                                    controlPoint2.x, controlPoint2.y,
                                    p1.x, p1.y
                                )
                            }

                            fillPath.lineTo(points.last().x, height)
                            fillPath.close()

                            // Draw gradient fill under curve
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        PennyCoralExpense.copy(alpha = 0.28f),
                                        PennyCoralExpense.copy(alpha = 0.02f)
                                    ),
                                    startY = 0f,
                                    endY = height
                                )
                            )

                            // Draw line
                            drawPath(
                                path = strokePath,
                                color = PennyCoralExpense,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Draw scrubber line & indicator dot if selected
                            val activeIdx = selectedIndex ?: (points.size - 1)
                            if (activeIdx in points.indices) {
                                val selectedPoint = points[activeIdx]
                                drawLine(
                                    color = PennyCoralExpense.copy(alpha = 0.5f),
                                    start = Offset(selectedPoint.x, 0f),
                                    end = Offset(selectedPoint.x, height),
                                    strokeWidth = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )
                                drawCircle(
                                    color = SlateSurface,
                                    radius = 7.dp.toPx(),
                                    center = selectedPoint
                                )
                                drawCircle(
                                    color = PennyCoralExpense,
                                    radius = 4.5.dp.toPx(),
                                    center = selectedPoint
                                )
                            }
                        }
                    }
                }

                // Bottom Day Axis Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val first = dayPoints.firstOrNull()?.dayOfMonth ?: 1
                    val mid = dayPoints.getOrNull(dayPoints.size / 2)?.dayOfMonth ?: 15
                    val last = dayPoints.lastOrNull()?.dayOfMonth ?: 30
                    Text(text = "Day $first", style = MaterialTheme.typography.labelSmall.copy(color = SlateTextMuted))
                    Text(text = "Day $mid", style = MaterialTheme.typography.labelSmall.copy(color = SlateTextMuted))
                    Text(text = "Day $last", style = MaterialTheme.typography.labelSmall.copy(color = SlateTextMuted))
                }
            }
        }
    }
}
