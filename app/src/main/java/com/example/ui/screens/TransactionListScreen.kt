package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CategoryRegistry
import com.example.data.model.TransactionEntity
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
import com.example.ui.viewmodel.PeriodFilter
import com.example.ui.viewmodel.TypeFilter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TransactionListScreen(
    viewModel: FinanceViewModel,
    onNavigateToAddTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val typeFilter by viewModel.typeFilter.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.categoryFilter.collectAsStateWithLifecycle()
    val periodFilter by viewModel.dashboardPeriod.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    // Filter transactions
    val filteredTransactions by remember(allTransactions, searchQuery, typeFilter, selectedCategory, periodFilter) {
        derivedStateOf {
            val periodFiltered = viewModel.filterByPeriod(allTransactions, periodFilter)
            periodFiltered.filter { tx ->
                val matchesType = when (typeFilter) {
                    TypeFilter.ALL -> true
                    TypeFilter.INCOME -> tx.isIncome
                    TypeFilter.EXPENSE -> tx.isExpense
                }
                val matchesCategory = selectedCategory == null || tx.category.equals(selectedCategory, ignoreCase = true)
                val matchesSearch = searchQuery.isBlank() ||
                        tx.title.contains(searchQuery, ignoreCase = true) ||
                        tx.category.contains(searchQuery, ignoreCase = true) ||
                        tx.note.contains(searchQuery, ignoreCase = true)
                matchesType && matchesCategory && matchesSearch
            }
        }
    }

    // Group by Date formatted header
    val groupedByDate by remember(filteredTransactions) {
        derivedStateOf {
            val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val todayStr = dayFormat.format(Date())
            val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val yesterdayStr = dayFormat.format(yesterdayCal.time)

            filteredTransactions.groupBy { tx ->
                val dateStr = dayFormat.format(Date(tx.timestamp))
                when (dateStr) {
                    todayStr -> "Today"
                    yesterdayStr -> "Yesterday"
                    else -> displayFormat.format(Date(tx.timestamp))
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${filteredTransactions.size} records found",
                    style = MaterialTheme.typography.labelMedium.copy(color = SlateTextSecondary)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by name, note, or category...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = SlateTextSecondary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = SlateTextSecondary)
                    }
                }
            },
            singleLine = true,
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
                .testTag("transaction_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Type Filter Pills (All, Income, Expense)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeFilter.entries.forEach { filter ->
                val isSelected = typeFilter == filter
                val label = when (filter) {
                    TypeFilter.ALL -> "All"
                    TypeFilter.INCOME -> "Income"
                    TypeFilter.EXPENSE -> "Expense"
                }
                val pillColor = if (isSelected) {
                    when (filter) {
                        TypeFilter.ALL -> MaterialTheme.colorScheme.primary
                        TypeFilter.INCOME -> PennyGreenPrimary
                        TypeFilter.EXPENSE -> PennyCoralExpense
                    }
                } else SlateSurface

                Surface(
                    onClick = { viewModel.setTypeFilter(filter) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) pillColor.copy(alpha = 0.15f) else SlateSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) pillColor else SlateBorder),
                    modifier = Modifier.weight(1f).testTag("filter_type_${filter.name}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) pillColor else SlateTextSecondary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Horizontal Scroll
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isAll = selectedCategory == null
                Surface(
                    onClick = { viewModel.setCategoryFilter(null) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAll) MaterialTheme.colorScheme.primary else SlateSurfaceSubtle,
                    modifier = Modifier.testTag("filter_cat_all")
                ) {
                    Text(
                        text = "All Categories",
                        color = if (isAll) SlateSurface else SlateTextSecondary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            items(CategoryRegistry.allCategories, key = { it.name }) { cat ->
                val isSelected = selectedCategory == cat.name
                Surface(
                    onClick = { viewModel.setCategoryFilter(if (isSelected) null else cat.name) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) cat.color else SlateSurfaceSubtle,
                    modifier = Modifier.testTag("filter_cat_${cat.name}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = null,
                            tint = if (isSelected) SlateSurface else cat.color,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cat.name,
                            color = if (isSelected) SlateSurface else CharcoalTextPrimary,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transaction List Grouped by Date
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No matching transactions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your filters or search keywords.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextMuted)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                groupedByDate.forEach { (dateHeader, transactionsInDate) ->
                    item(key = "header_$dateHeader") {
                        Text(
                            text = dateHeader,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                    }

                    items(transactionsInDate, key = { it.id }) { tx ->
                        TransactionItemCard(
                            transaction = tx,
                            formattedAmount = viewModel.formatAmount(tx.amount, withSign = true, isIncome = tx.isIncome),
                            onDelete = { viewModel.deleteTransaction(tx.id) }
                        )
                    }
                }
            }
        }
    }
}
