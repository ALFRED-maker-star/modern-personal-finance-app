package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryItem
import com.example.data.model.CategoryRegistry
import com.example.data.model.TransactionType
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var rawAmountInput by remember { mutableStateOf("0") }
    var selectedCategory by remember {
        mutableStateOf(CategoryRegistry.expenseCategories.first())
    }
    var titleInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Update default category when switching type
    fun onTypeSwitched(type: TransactionType) {
        selectedType = type
        selectedCategory = if (type == TransactionType.EXPENSE) {
            CategoryRegistry.expenseCategories.first()
        } else {
            CategoryRegistry.incomeCategories.first()
        }
    }

    // Keypad handler
    fun handleKeypadPress(key: String) {
        when (key) {
            "⌫" -> {
                if (rawAmountInput.length > 1) {
                    rawAmountInput = rawAmountInput.dropLast(1)
                } else {
                    rawAmountInput = "0"
                }
            }
            "." -> {
                if (!rawAmountInput.contains(".")) {
                    rawAmountInput += "."
                }
            }
            else -> {
                if (rawAmountInput == "0") {
                    rawAmountInput = key
                } else {
                    // Limit to 2 decimal places
                    val parts = rawAmountInput.split(".")
                    if (parts.size == 2 && parts[1].length >= 2) {
                        return
                    }
                    if (rawAmountInput.length < 9) {
                        rawAmountInput += key
                    }
                }
            }
        }
    }

    val parsedAmount = rawAmountInput.toDoubleOrNull() ?: 0.0
    val activeColor = if (selectedType == TransactionType.EXPENSE) PennyCoralExpense else PennyGreenPrimary

    val scrollState = rememberScrollState()
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // Top Header
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
                    .testTag("add_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CharcoalTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "New Transaction",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Income / Expense Toggle Segment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SlateSurfaceSubtle)
                .padding(4.dp)
        ) {
            val isExpense = selectedType == TransactionType.EXPENSE
            // Expense Tab
            Surface(
                selected = isExpense,
                onClick = { onTypeSwitched(TransactionType.EXPENSE) },
                shape = RoundedCornerShape(12.dp),
                color = if (isExpense) PennyCoralContainer else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .testTag("toggle_expense_tab")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Expense",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isExpense) PennyCoralExpense else SlateTextSecondary
                        )
                    )
                }
            }

            // Income Tab
            Surface(
                selected = !isExpense,
                onClick = { onTypeSwitched(TransactionType.INCOME) },
                shape = RoundedCornerShape(12.dp),
                color = if (!isExpense) PennyGreenContainer else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .testTag("toggle_income_tab")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "Income",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (!isExpense) PennyGreenPrimary else SlateTextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Amount Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (selectedType == TransactionType.EXPENSE) "YOU SPENT" else "YOU RECEIVED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = settings.currencySymbol,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = activeColor
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = rawAmountInput,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CharcoalTextPrimary
                        ),
                        modifier = Modifier.testTag("amount_display_text")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Numeric Keypad Grid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder))
        ) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(".", "0", "⌫")
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                keys.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowKeys.forEach { key ->
                            Surface(
                                onClick = { handleKeypadPress(key) },
                                shape = RoundedCornerShape(14.dp),
                                color = SlateSurfaceSubtle,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("keypad_$key")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (key == "⌫") {
                                        Icon(
                                            imageVector = Icons.Default.Backspace,
                                            contentDescription = "Backspace",
                                            tint = CharcoalTextPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = CharcoalTextPrimary
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

        Spacer(modifier = Modifier.height(18.dp))

        // Category Picker (Icon Grid)
        Text(
            text = "SELECT CATEGORY",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextSecondary
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val currentCategories = if (selectedType == TransactionType.EXPENSE) {
            CategoryRegistry.expenseCategories
        } else {
            CategoryRegistry.incomeCategories
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentCategories, key = { it.name }) { cat ->
                val isSelected = selectedCategory.name == cat.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) cat.color.copy(alpha = 0.15f) else SlateSurface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) cat.color else SlateBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedCategory = cat }
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                        .testTag("category_picker_${cat.name}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) cat.color else cat.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = cat.name,
                            tint = if (isSelected) SlateSurface else cat.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = cat.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = CharcoalTextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Date Picker Selector
        Text(
            text = "DATE",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextSecondary
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val nowCal = Calendar.getInstance()
            val isToday = (nowCal.timeInMillis - selectedTimestamp) < (24 * 60 * 60 * 1000)

            // Today chip
            Surface(
                onClick = { selectedTimestamp = System.currentTimeMillis() },
                shape = RoundedCornerShape(12.dp),
                color = if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else SlateSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isToday) MaterialTheme.colorScheme.primary else SlateBorder),
                modifier = Modifier.weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (isToday) MaterialTheme.colorScheme.primary else CharcoalTextPrimary
                        )
                    )
                }
            }

            // Custom Date Selector Button
            Surface(
                onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val newCal = Calendar.getInstance()
                            newCal.set(y, m, d)
                            selectedTimestamp = newCal.timeInMillis
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                shape = RoundedCornerShape(12.dp),
                color = SlateSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .weight(2f)
                    .testTag("custom_date_picker_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SlateTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dateFormat.format(Date(selectedTimestamp)),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalTextPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payee / Title Input Field
        Text(
            text = "PAYEE / TITLE & NOTES",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextSecondary
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            placeholder = { Text("e.g. Starbucks, Monthly Salary, Rent") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = SlateTextSecondary)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateSurface,
                unfocusedContainerColor = SlateSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = SlateBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("transaction_title_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = noteInput,
            onValueChange = { noteInput = it },
            placeholder = { Text("Optional note or receipt details...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = SlateTextSecondary)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateSurface,
                unfocusedContainerColor = SlateSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = SlateBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("transaction_note_input")
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                if (parsedAmount > 0.0) {
                    val finalTitle = titleInput.ifBlank { selectedCategory.name }
                    viewModel.addTransaction(
                        title = finalTitle,
                        amount = parsedAmount,
                        type = selectedType,
                        category = selectedCategory.name,
                        timestamp = selectedTimestamp,
                        note = noteInput
                    )
                    onNavigateBack()
                }
            },
            enabled = parsedAmount > 0.0,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = activeColor,
                contentColor = SlateSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 16.dp)
                .testTag("save_transaction_button")
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save Transaction",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
