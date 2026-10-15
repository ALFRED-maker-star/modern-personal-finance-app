package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME,
    EXPENSE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val timestamp: Long,
    val note: String = ""
) {
    val isExpense: Boolean get() = type == TransactionType.EXPENSE.name
    val isIncome: Boolean get() = type == TransactionType.INCOME.name
}
