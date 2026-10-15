package com.example.data.model

data class CurrencyOption(
    val code: String,
    val symbol: String,
    val name: String
)

val SupportedCurrencies = listOf(
    CurrencyOption("USD", "$", "US Dollar ($)"),
    CurrencyOption("EUR", "€", "Euro (€)"),
    CurrencyOption("GBP", "£", "British Pound (£)"),
    CurrencyOption("JPY", "¥", "Japanese Yen (¥)"),
    CurrencyOption("CAD", "CA$", "Canadian Dollar (CA$)"),
    CurrencyOption("AUD", "AU$", "Australian Dollar (AU$)"),
    CurrencyOption("INR", "₹", "Indian Rupee (₹)"),
    CurrencyOption("NGN", "₦", "Nigerian Naira (₦)")
)

data class UserSettings(
    val currencyCode: String = "USD",
    val currencySymbol: String = "$",
    val dailyReminderEnabled: Boolean = true,
    val budgetAlertsEnabled: Boolean = true,
    val weeklyReportEnabled: Boolean = true
)
