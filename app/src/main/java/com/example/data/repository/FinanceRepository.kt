package com.example.data.repository

import com.example.data.local.BudgetDao
import com.example.data.local.TransactionDao
import com.example.data.model.BudgetEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteById(id)
    }

    suspend fun insertBudget(budget: BudgetEntity): Long {
        return budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }

    suspend fun checkAndSeedInitialData() {
        if (transactionDao.getCount() == 0) {
            seedSampleData()
        }
    }

    suspend fun resetToSampleData() {
        transactionDao.deleteAll()
        budgetDao.deleteAll()
        seedSampleData()
    }

    suspend fun clearAllData() {
        transactionDao.deleteAll()
        budgetDao.deleteAll()
    }

    suspend fun seedSampleData() {
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance()

        fun dateOffset(daysAgo: Int, hour: Int = 14, minute: Int = 30): Long {
            calendar.timeInMillis = now.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            return calendar.timeInMillis
        }

        // Realistic seed transactions across this month
        val sampleTransactions = listOf(
            TransactionEntity(
                title = "Monthly Salary - TechCorp",
                amount = 4500.00,
                type = TransactionType.INCOME.name,
                category = "Salary",
                timestamp = dateOffset(13, 9, 0),
                note = "Bi-weekly paycheck direct deposit"
            ),
            TransactionEntity(
                title = "Freelance UI Design Project",
                amount = 850.00,
                type = TransactionType.INCOME.name,
                category = "Freelance",
                timestamp = dateOffset(4, 16, 20),
                note = "Client milestone payment for mobile app mockup"
            ),
            TransactionEntity(
                title = "Stock Dividend Payout",
                amount = 120.50,
                type = TransactionType.INCOME.name,
                category = "Investment",
                timestamp = dateOffset(9, 10, 0),
                note = "Quarterly index fund dividend"
            ),
            TransactionEntity(
                title = "Whole Foods Organic Market",
                amount = 138.45,
                type = TransactionType.EXPENSE.name,
                category = "Groceries",
                timestamp = dateOffset(0, 18, 15),
                note = "Weekly groceries, produce & pantry restock"
            ),
            TransactionEntity(
                title = "Blue Bottle Artisan Cafe",
                amount = 14.50,
                type = TransactionType.EXPENSE.name,
                category = "Dining",
                timestamp = dateOffset(0, 9, 30),
                note = "Oat milk latte & almond croissant"
            ),
            TransactionEntity(
                title = "Trattoria Pasta & Wine Bar",
                amount = 76.80,
                type = TransactionType.EXPENSE.name,
                category = "Dining",
                timestamp = dateOffset(1, 20, 0),
                note = "Dinner with Sarah & Marco"
            ),
            TransactionEntity(
                title = "Trader Joe's",
                amount = 84.20,
                type = TransactionType.EXPENSE.name,
                category = "Groceries",
                timestamp = dateOffset(3, 17, 45),
                note = "Snacks, frozen meals, coffee beans"
            ),
            TransactionEntity(
                title = "Uber Ride downtown",
                amount = 26.50,
                type = TransactionType.EXPENSE.name,
                category = "Transport",
                timestamp = dateOffset(2, 22, 10),
                note = "Ride home after dinner"
            ),
            TransactionEntity(
                title = "Metro Transit Pass",
                amount = 65.00,
                type = TransactionType.EXPENSE.name,
                category = "Transport",
                timestamp = dateOffset(12, 8, 30),
                note = "Monthly subway & bus pass"
            ),
            TransactionEntity(
                title = "Nordstrom Fall Jacket",
                amount = 185.00,
                type = TransactionType.EXPENSE.name,
                category = "Shopping",
                timestamp = dateOffset(5, 15, 30),
                note = "Waterproof trench coat"
            ),
            TransactionEntity(
                title = "Amazon Electronics",
                amount = 62.99,
                type = TransactionType.EXPENSE.name,
                category = "Shopping",
                timestamp = dateOffset(7, 14, 0),
                note = "USB-C charging hub & braided cables"
            ),
            TransactionEntity(
                title = "Apartment Rent",
                amount = 1450.00,
                type = TransactionType.EXPENSE.name,
                category = "Housing",
                timestamp = dateOffset(13, 8, 0),
                note = "Monthly apartment rent"
            ),
            TransactionEntity(
                title = "Electric & Gas Utility",
                amount = 112.40,
                type = TransactionType.EXPENSE.name,
                category = "Utilities",
                timestamp = dateOffset(8, 11, 0),
                note = "City Power & Light"
            ),
            TransactionEntity(
                title = "Fiber Internet Bill",
                amount = 69.99,
                type = TransactionType.EXPENSE.name,
                category = "Utilities",
                timestamp = dateOffset(10, 10, 30),
                note = "High speed gigabit fiber"
            ),
            TransactionEntity(
                title = "Equinox Gym Membership",
                amount = 140.00,
                type = TransactionType.EXPENSE.name,
                category = "Health",
                timestamp = dateOffset(11, 7, 30),
                note = "Monthly fitness club membership"
            ),
            TransactionEntity(
                title = "Netflix & Spotify Premium",
                amount = 32.98,
                type = TransactionType.EXPENSE.name,
                category = "Entertainment",
                timestamp = dateOffset(6, 12, 0),
                note = "Monthly digital streaming bundles"
            ),
            TransactionEntity(
                title = "AMC IMAX Movie Night",
                amount = 44.50,
                type = TransactionType.EXPENSE.name,
                category = "Entertainment",
                timestamp = dateOffset(4, 21, 15),
                note = "Tickets and popcorn combo"
            ),
            TransactionEntity(
                title = "CVS Pharmacy",
                amount = 28.75,
                type = TransactionType.EXPENSE.name,
                category = "Medical",
                timestamp = dateOffset(9, 16, 45),
                note = "Vitamins & allergy relief"
            )
        )

        // Realistic seed budgets
        val sampleBudgets = listOf(
            BudgetEntity(category = "Groceries", monthlyLimit = 400.00),
            BudgetEntity(category = "Dining", monthlyLimit = 150.00), // Exceeded on purpose to show over-budget coral red warning!
            BudgetEntity(category = "Shopping", monthlyLimit = 300.00),
            BudgetEntity(category = "Transport", monthlyLimit = 120.00),
            BudgetEntity(category = "Housing", monthlyLimit = 1500.00),
            BudgetEntity(category = "Utilities", monthlyLimit = 220.00),
            BudgetEntity(category = "Entertainment", monthlyLimit = 100.00),
            BudgetEntity(category = "Health", monthlyLimit = 180.00)
        )

        transactionDao.insertTransactions(sampleTransactions)
        budgetDao.insertBudgets(sampleBudgets)
    }

    fun exportToCsv(transactions: List<TransactionEntity>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("ID,Date,Type,Category,Title,Amount,Note\n")
        transactions.forEach { t ->
            val dateStr = dateFormat.format(Date(t.timestamp))
            val escapedTitle = t.title.replace("\"", "\"\"")
            val escapedNote = t.note.replace("\"", "\"\"")
            sb.append("${t.id},\"$dateStr\",${t.type},\"${t.category}\",\"$escapedTitle\",${t.amount},\"$escapedNote\"\n")
        }
        return sb.toString()
    }
}
