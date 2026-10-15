package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.PennyCoralContainer
import com.example.ui.theme.PennyCoralExpense
import com.example.ui.theme.PennyCoralText
import com.example.ui.theme.PennyGreenContainer
import com.example.ui.theme.PennyGreenPrimary
import com.example.ui.theme.PennyGreenText
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.BudgetProgress
import com.example.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val budgetProgressList by viewModel.budgetProgressList.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    var showBudgetDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<String?>(null) }
    var limitInput by remember { mutableStateOf("") }
    var selectedCategoryDropdown by remember { mutableStateOf(CategoryRegistry.expenseCategories.first().name) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val totalBudget = budgetProgressList.sumOf { it.limit }
    val totalSpent = budgetProgressList.sumOf { it.spent }
    val overBudgetCount = budgetProgressList.count { it.isOverBudget }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Budget Setup",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Category limits & spending alerts",
                            style = MaterialTheme.typography.labelMedium.copy(color = SlateTextSecondary)
                        )
                    }

                    Surface(
                        onClick = {
                            editingCategory = null
                            selectedCategoryDropdown = CategoryRegistry.expenseCategories.first().name
                            limitInput = ""
                            showBudgetDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = PennyGreenPrimary,
                        modifier = Modifier.testTag("add_budget_top_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = SlateSurface, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Set Budget",
                                color = SlateSurface,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Overall Budget Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
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
                                    text = "MONTHLY TOTAL BUDGET",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${settings.currencySymbol}${formatter.format(totalBudget)}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalTextPrimary
                                    )
                                )
                            }

                            if (overBudgetCount > 0) {
                                Surface(
                                    color = PennyCoralContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = PennyCoralExpense,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$overBudgetCount Over Limit",
                                            color = PennyCoralText,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    color = PennyGreenContainer,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "On Track",
                                        color = PennyGreenText,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Total Spent vs Total Budget progress
                        val totalRatio = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(SlateSurfaceSubtle)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(totalRatio.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (totalSpent > totalBudget) PennyCoralExpense else PennyGreenPrimary)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: ${settings.currencySymbol}${formatter.format(totalSpent)}",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                            val remaining = totalBudget - totalSpent
                            Text(
                                text = if (remaining >= 0) "Remaining: ${settings.currencySymbol}${formatter.format(remaining)}"
                                else "Over: ${settings.currencySymbol}${formatter.format(-remaining)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (remaining >= 0) PennyGreenPrimary else PennyCoralExpense
                                )
                            )
                        }
                    }
                }
            }

            // Categories Section Title
            item {
                Text(
                    text = "CATEGORY BUDGETS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Budget Category Cards
            if (budgetProgressList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No category budgets configured yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap 'Set Budget' above to establish your first monthly target.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                            )
                        }
                    }
                }
            } else {
                items(budgetProgressList, key = { it.id }) { item ->
                    val meta = CategoryRegistry.getCategory(item.category, isExpense = true)
                    val isOver = item.isOverBudget
                    val progressRatio = item.progress.coerceIn(0f, 1f)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_card_${item.category}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isOver) PennyCoralContainer.copy(alpha = 0.45f) else SlateSurface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(if (isOver) PennyCoralExpense.copy(alpha = 0.5f) else SlateBorder)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(meta.color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = meta.icon,
                                        contentDescription = meta.name,
                                        tint = meta.color,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.category,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalTextPrimary
                                            )
                                        )
                                        if (isOver) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = PennyCoralExpense,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "OVER LIMIT",
                                                    color = SlateSurface,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${settings.currencySymbol}${formatter.format(item.spent)} of ${settings.currencySymbol}${formatter.format(item.limit)} limit",
                                        style = MaterialTheme.typography.labelMedium.copy(color = SlateTextSecondary)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            editingCategory = item.category
                                            selectedCategoryDropdown = item.category
                                            limitInput = item.limit.toInt().toString()
                                            showBudgetDialog = true
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Budget",
                                            tint = SlateTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteBudget(item.id) },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Budget",
                                            tint = SlateTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SlateSurfaceSubtle)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressRatio)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isOver) PennyCoralExpense else meta.color)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${(item.progress * 100).toInt()}% used",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOver) PennyCoralExpense else SlateTextSecondary
                                    )
                                )

                                Text(
                                    text = if (isOver) "Over budget by ${settings.currencySymbol}${formatter.format(item.spent - item.limit)}"
                                    else "${settings.currencySymbol}${formatter.format(item.remaining)} left",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isOver) PennyCoralExpense else PennyGreenPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Set / Edit Budget Dialog
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = {
                Text(
                    text = if (editingCategory != null) "Edit $editingCategory Budget" else "Set Monthly Category Budget",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    if (editingCategory == null) {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SlateTextSecondary)
                        )
                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategoryDropdown,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                CategoryRegistry.expenseCategories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        leadingIcon = {
                                            Icon(imageVector = cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(18.dp))
                                        },
                                        onClick = {
                                            selectedCategoryDropdown = cat.name
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Monthly Spending Limit (${settings.currencySymbol})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SlateTextSecondary)
                    )
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it },
                        placeholder = { Text("e.g. 350.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_limit_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = limitInput.toDoubleOrNull() ?: 0.0
                        if (amount > 0.0) {
                            val cat = editingCategory ?: selectedCategoryDropdown
                            viewModel.saveBudget(cat, amount)
                            showBudgetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PennyGreenPrimary),
                    modifier = Modifier.testTag("confirm_budget_save_button")
                ) {
                    Text("Save Limit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }
}
