package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryRegistry
import com.example.data.model.TransactionEntity
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.PennyCoralContainer
import com.example.ui.theme.PennyCoralExpense
import com.example.ui.theme.PennyCoralText
import com.example.ui.theme.PennyGreenContainer
import com.example.ui.theme.PennyGreenPrimary
import com.example.ui.theme.PennyGreenText
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BalanceHeroCard(
    balance: Double,
    currencySymbol: String,
    formattedBalance: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp), ambientColor = Color(0x10059669), spotColor = Color(0x15059669)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            PennyGreenPrimary.copy(alpha = 0.08f),
                            SlateSurface
                        )
                    )
                )
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TOTAL BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )
                    )
                    Surface(
                        color = PennyGreenContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Live Active",
                            color = PennyGreenText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = formattedBalance,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = CharcoalTextPrimary
                    ),
                    modifier = Modifier.testTag("current_balance_text")
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Across all linked accounts & cash logs",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                )
            }
        }
    }
}

@Composable
fun IncomeExpenseSummaryRow(
    incomeText: String,
    expenseText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Income card
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x08000000)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PennyGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Income",
                            tint = PennyGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextSecondary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = incomeText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PennyGreenPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Expense card
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x08000000)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PennyCoralContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = "Expense",
                            tint = PennyCoralExpense,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = SlateTextSecondary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = expenseText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PennyCoralExpense
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    formattedAmount: String,
    onDelete: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val categoryItem = CategoryRegistry.getCategory(transaction.category, transaction.isExpense)
    val timeFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    val dateString = timeFormat.format(Date(transaction.timestamp))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(SlateBorder.copy(alpha = 0.6f), SlateBorder)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with tinted background
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(categoryItem.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryItem.icon,
                    contentDescription = categoryItem.name,
                    tint = categoryItem.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalTextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = categoryItem.color,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = " • $dateString",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextMuted),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (transaction.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SlateTextSecondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formattedAmount,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.isIncome) PennyGreenPrimary else PennyCoralExpense
                    )
                )
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_transaction_${transaction.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete transaction",
                            tint = SlateTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
