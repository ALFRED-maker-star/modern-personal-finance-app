package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryRegistry
import com.example.data.model.CurrencyOption
import com.example.data.model.SupportedCurrencies
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserSettings
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class PeriodFilter {
    THIS_MONTH,
    THIS_WEEK,
    ALL_TIME
}

enum class TypeFilter {
    ALL,
    INCOME,
    EXPENSE
}

data class CategorySpend(
    val category: String,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)

data class BudgetProgress(
    val id: Long,
    val category: String,
    val spent: Double,
    val limit: Double,
    val progress: Float, // 0f to 1f+
    val remaining: Double,
    val isOverBudget: Boolean
)

data class DaySpend(
    val dayOfMonth: Int,
    val dayLabel: String,
    val amount: Double
)

data class PeriodComparison(
    val label: String,
    val income: Double,
    val expense: Double
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FinanceRepository(database.transactionDao(), database.budgetDao())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()

    // Dashboard toggle period
    private val _dashboardPeriod = MutableStateFlow(PeriodFilter.THIS_MONTH)
    val dashboardPeriod: StateFlow<PeriodFilter> = _dashboardPeriod.asStateFlow()

    // Transaction list filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _typeFilter = MutableStateFlow(TypeFilter.ALL)
    val typeFilter: StateFlow<TypeFilter> = _typeFilter.asStateFlow()

    private val _categoryFilter = MutableStateFlow<String?>(null)
    val categoryFilter: StateFlow<String?> = _categoryFilter.asStateFlow()

    // Drill-down selected category for category breakdown screen
    private val _selectedDrillCategory = MutableStateFlow<String?>(null)
    val selectedDrillCategory: StateFlow<String?> = _selectedDrillCategory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun setDashboardPeriod(period: PeriodFilter) {
        _dashboardPeriod.value = period
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(filter: TypeFilter) {
        _typeFilter.value = filter
    }

    fun setCategoryFilter(category: String?) {
        _categoryFilter.value = category
    }

    fun setSelectedDrillCategory(category: String?) {
        _selectedDrillCategory.value = category
    }

    fun setCurrency(currency: CurrencyOption) {
        _userSettings.value = _userSettings.value.copy(
            currencyCode = currency.code,
            currencySymbol = currency.symbol
        )
    }

    fun updateNotificationPreferences(
        dailyReminder: Boolean,
        budgetAlerts: Boolean,
        weeklyReport: Boolean
    ) {
        _userSettings.value = _userSettings.value.copy(
            dailyReminderEnabled = dailyReminder,
            budgetAlertsEnabled = budgetAlerts,
            weeklyReportEnabled = weeklyReport
        )
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        timestamp: Long,
        note: String
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                title = title.ifBlank { category },
                amount = amount,
                type = type.name,
                category = category,
                timestamp = timestamp,
                note = note.trim()
            )
            repository.insertTransaction(entity)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    fun saveBudget(category: String, limit: Double) {
        viewModelScope.launch {
            val existing = allBudgets.value.find { it.category.equals(category, ignoreCase = true) }
            if (existing != null) {
                repository.updateBudget(existing.copy(monthlyLimit = limit))
            } else {
                repository.insertBudget(BudgetEntity(category = category, monthlyLimit = limit))
            }
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch {
            repository.deleteBudget(BudgetEntity(id = id, category = "", monthlyLimit = 0.0))
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun exportTransactionsCsv(): String {
        return repository.exportToCsv(allTransactions.value)
    }

    // Helper to format currency
    fun formatAmount(amount: Double, withSign: Boolean = false, isIncome: Boolean = false): String {
        val symbol = userSettings.value.currencySymbol
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        val formattedNumber = formatter.format(Math.abs(amount))
        return if (withSign) {
            if (isIncome) "+$symbol$formattedNumber" else "-$symbol$formattedNumber"
        } else {
            "$symbol$formattedNumber"
        }
    }

    // Filter transactions by period
    fun filterByPeriod(list: List<TransactionEntity>, period: PeriodFilter): List<TransactionEntity> {
        val now = Calendar.getInstance()
        return when (period) {
            PeriodFilter.THIS_MONTH -> {
                val startOfMonth = (now.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                list.filter { it.timestamp >= startOfMonth }
            }
            PeriodFilter.THIS_WEEK -> {
                val startOfWeek = (now.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                list.filter { it.timestamp >= startOfWeek }
            }
            PeriodFilter.ALL_TIME -> list
        }
    }

    // This month's stats
    val thisMonthStats = combine(allTransactions, _dashboardPeriod) { transactions, period ->
        val filtered = filterByPeriod(transactions, period)
        val income = filtered.filter { it.isIncome }.sumOf { it.amount }
        val expense = filtered.filter { it.isExpense }.sumOf { it.amount }
        val allIncome = transactions.filter { it.isIncome }.sumOf { it.amount }
        val allExpense = transactions.filter { it.isExpense }.sumOf { it.amount }
        val totalBalance = allIncome - allExpense
        val netSavings = income - expense

        // Spending by category for current period
        val expenses = filtered.filter { it.isExpense }
        val totalPeriodExpense = expenses.sumOf { it.amount }
        val categorySpends = expenses
            .groupBy { it.category }
            .map { (cat, items) ->
                val sum = items.sumOf { it.amount }
                CategorySpend(
                    category = cat,
                    totalAmount = sum,
                    percentage = if (totalPeriodExpense > 0) (sum / totalPeriodExpense).toFloat() else 0f,
                    transactionCount = items.size
                )
            }
            .sortedByDescending { it.totalAmount }

        DashboardStats(
            totalBalance = totalBalance,
            periodIncome = income,
            periodExpense = expense,
            netSavings = netSavings,
            categorySpends = categorySpends,
            recentTransactions = transactions.take(6)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardStats(0.0, 0.0, 0.0, 0.0, emptyList(), emptyList())
    )

    // Budget progress
    val budgetProgressList = combine(allTransactions, allBudgets) { transactions, budgets ->
        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val thisMonthExpenses = transactions.filter { it.isExpense && it.timestamp >= startOfMonth }
        val spentByCategory = thisMonthExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        budgets.map { b ->
            val spent = spentByCategory[b.category] ?: 0.0
            val progress = if (b.monthlyLimit > 0) (spent / b.monthlyLimit).toFloat() else 0f
            val remaining = b.monthlyLimit - spent
            val isOver = spent > b.monthlyLimit
            BudgetProgress(
                id = b.id,
                category = b.category,
                spent = spent,
                limit = b.monthlyLimit,
                progress = progress,
                remaining = remaining,
                isOverBudget = isOver
            )
        }.sortedByDescending { it.isOverBudget }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly Report Data (Line chart points & comparison bars)
    val monthlyReportData = allTransactions.combine(_dashboardPeriod) { transactions, _ ->
        val now = Calendar.getInstance()
        val daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = now.get(Calendar.DAY_OF_MONTH)

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfMonth = cal.timeInMillis

        val monthTransactions = transactions.filter { it.timestamp >= startOfMonth }
        val monthExpenses = monthTransactions.filter { it.isExpense }
        val totalExpense = monthExpenses.sumOf { it.amount }
        val monthIncome = monthTransactions.filter { it.isIncome }
        val totalIncome = monthIncome.sumOf { it.amount }

        // Day by day spending for line chart
        val daySpendMap = mutableMapOf<Int, Double>()
        for (day in 1..currentDay) {
            daySpendMap[day] = 0.0
        }
        val dayCal = Calendar.getInstance()
        monthExpenses.forEach { t ->
            dayCal.timeInMillis = t.timestamp
            val day = dayCal.get(Calendar.DAY_OF_MONTH)
            daySpendMap[day] = (daySpendMap[day] ?: 0.0) + t.amount
        }
        val linePoints = daySpendMap.entries.sortedBy { it.key }.map { (day, amount) ->
            DaySpend(dayOfMonth = day, dayLabel = "$day", amount = amount)
        }

        // 4-week comparison bars
        val weekComparisons = mutableListOf<PeriodComparison>()
        for (w in 1..4) {
            val startDay = (w - 1) * 7 + 1
            val endDay = if (w == 4) daysInMonth else w * 7
            val weekExp = monthExpenses.filter {
                dayCal.timeInMillis = it.timestamp
                val d = dayCal.get(Calendar.DAY_OF_MONTH)
                d in startDay..endDay
            }.sumOf { it.amount }
            val weekInc = monthIncome.filter {
                dayCal.timeInMillis = it.timestamp
                val d = dayCal.get(Calendar.DAY_OF_MONTH)
                d in startDay..endDay
            }.sumOf { it.amount }
            weekComparisons.add(PeriodComparison("W$w", weekInc, weekExp))
        }

        // Top 5 spending categories
        val topCategories = monthExpenses.groupBy { it.category }
            .map { (cat, list) ->
                val sum = list.sumOf { it.amount }
                CategorySpend(
                    category = cat,
                    totalAmount = sum,
                    percentage = if (totalExpense > 0) (sum / totalExpense).toFloat() else 0f,
                    transactionCount = list.size
                )
            }
            .sortedByDescending { it.totalAmount }
            .take(5)

        val highestDay = linePoints.maxByOrNull { it.amount }
        val dailyAverage = if (currentDay > 0) totalExpense / currentDay else 0.0
        val savingsRate = if (totalIncome > 0) ((totalIncome - totalExpense) / totalIncome) * 100 else 0.0

        MonthlyReportStats(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netSavings = totalIncome - totalExpense,
            dailyAverage = dailyAverage,
            savingsRate = savingsRate,
            highestSpendDay = highestDay,
            dayPoints = linePoints,
            weekBars = weekComparisons,
            topCategories = topCategories
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MonthlyReportStats(0.0, 0.0, 0.0, 0.0, 0.0, null, emptyList(), emptyList(), emptyList())
    )
}

data class DashboardStats(
    val totalBalance: Double,
    val periodIncome: Double,
    val periodExpense: Double,
    val netSavings: Double,
    val categorySpends: List<CategorySpend>,
    val recentTransactions: List<TransactionEntity>
)

data class MonthlyReportStats(
    val totalIncome: Double,
    val totalExpense: Double,
    val netSavings: Double,
    val dailyAverage: Double,
    val savingsRate: Double,
    val highestSpendDay: DaySpend?,
    val dayPoints: List<DaySpend>,
    val weekBars: List<PeriodComparison>,
    val topCategories: List<CategorySpend>
)
