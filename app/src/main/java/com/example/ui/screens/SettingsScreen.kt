package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CurrencyOption
import com.example.data.model.SupportedCurrencies
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

@Composable
fun SettingsScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SlateSurface)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CharcoalTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Settings & Preferences",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Currency, export & alerts",
                        style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                    )
                }
            }
        }

        // Section: Currency Configuration
        item {
            Text(
                text = "CURRENCY & FORMATTING",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextSecondary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { showCurrencyDialog = true }
                    .testTag("currency_selector_setting"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PennyGreenPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = PennyGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Active Currency",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${settings.currencyCode} (${settings.currencySymbol})",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = SlateTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Section: Notification Preferences
        item {
            Text(
                text = "NOTIFICATIONS & SMART ALERTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextSecondary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Daily Expense Reminder Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Expense Logging Reminder",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Evening prompt at 8:00 PM to record daily receipts",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                        Switch(
                            checked = settings.dailyReminderEnabled,
                            onCheckedChange = {
                                viewModel.updateNotificationPreferences(
                                    dailyReminder = it,
                                    budgetAlerts = settings.budgetAlertsEnabled,
                                    weeklyReport = settings.weeklyReportEnabled
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = SlateSurface, checkedTrackColor = PennyGreenPrimary),
                            modifier = Modifier.testTag("switch_daily_reminder")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder.copy(alpha = 0.5f))

                    // Budget Alert Threshold
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Budget Limit Exceeded Warnings",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Immediate alert when a category spend exceeds 100%",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                        Switch(
                            checked = settings.budgetAlertsEnabled,
                            onCheckedChange = {
                                viewModel.updateNotificationPreferences(
                                    dailyReminder = settings.dailyReminderEnabled,
                                    budgetAlerts = it,
                                    weeklyReport = settings.weeklyReportEnabled
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = SlateSurface, checkedTrackColor = PennyGreenPrimary),
                            modifier = Modifier.testTag("switch_budget_alerts")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder.copy(alpha = 0.5f))

                    // Weekly Report Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Weekly Sunday Digest",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "Weekly spend roundup & savings rate report",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                        Switch(
                            checked = settings.weeklyReportEnabled,
                            onCheckedChange = {
                                viewModel.updateNotificationPreferences(
                                    dailyReminder = settings.dailyReminderEnabled,
                                    budgetAlerts = settings.budgetAlertsEnabled,
                                    weeklyReport = it
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = SlateSurface, checkedTrackColor = PennyGreenPrimary),
                            modifier = Modifier.testTag("switch_weekly_report")
                        )
                    }
                }
            }
        }

        // Section: Data & Export
        item {
            Text(
                text = "DATA MANAGEMENT & BACKUP",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextSecondary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SlateBorder))
            ) {
                Column {
                    // Export CSV
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showExportDialog = true }
                            .padding(16.dp)
                            .testTag("export_data_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = PennyGreenPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Export Data (CSV)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Export ${allTransactions.size} transactions to CSV spreadsheet",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = SlateTextMuted, modifier = Modifier.size(14.dp))
                    }

                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                    // Reset to Sample Data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(16.dp)
                            .testTag("reset_sample_data_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SlateSurfaceSubtle),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = SlateTextSecondary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reset to Sample Data",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Re-seed with realistic transactions and category budgets",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = SlateTextMuted, modifier = Modifier.size(14.dp))
                    }

                    HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f))

                    // Clear all data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.clearAllData()
                                Toast.makeText(context, "All records cleared", Toast.LENGTH_SHORT).show()
                            }
                            .padding(16.dp)
                            .testTag("clear_all_data_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PennyCoralExpense.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = PennyCoralExpense, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clear All Records",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PennyCoralExpense)
                            )
                            Text(
                                text = "Erase all transactions and start fresh",
                                style = MaterialTheme.typography.labelSmall.copy(color = SlateTextSecondary)
                            )
                        }
                    }
                }
            }
        }

        // Section: About
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = PennyGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PennyWise v1.0",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Designed for seamless mobile budgeting, real-time analytics, and category limits.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
                    )
                }
            }
        }
    }

    // Currency Selector Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = {
                Text(
                    text = "Select Currency",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SupportedCurrencies.forEach { option ->
                        val isSelected = settings.currencyCode == option.code
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PennyGreenPrimary.copy(alpha = 0.1f) else SlateSurface)
                                .clickable {
                                    viewModel.setCurrency(option)
                                    showCurrencyDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PennyGreenPrimary else CharcoalTextPrimary
                                )
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setCurrency(option)
                                    showCurrencyDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = PennyGreenPrimary)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close", color = SlateTextSecondary)
                }
            }
        )
    }

    // Export CSV Dialog
    if (showExportDialog) {
        val csvData = remember { viewModel.exportTransactionsCsv() }
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = "Export Transactions CSV",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ready to export ${allTransactions.size} transactions to CSV format.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SlateTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = SlateSurfaceSubtle,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    ) {
                        Text(
                            text = csvData.take(300) + if (csvData.length > 300) "\n..." else "",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("PennyWise Transactions", csvData))
                            Toast.makeText(context, "CSV copied to clipboard!", Toast.LENGTH_SHORT).show()
                            showExportDialog = false
                        }
                    ) {
                        Text("Copy CSV", color = PennyGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, csvData)
                                type = "text/csv"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Transactions CSV"))
                            showExportDialog = false
                        }
                    ) {
                        Text("Share", color = PennyGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = SlateTextSecondary)
                }
            }
        )
    }

    // Reset Sample Data Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Sample Data?") },
            text = {
                Text("This will replace current transactions and budgets with freshly loaded sample finance data.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToSampleData()
                        showResetDialog = false
                        Toast.makeText(context, "Sample data restored", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PennyGreenPrimary)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = SlateTextSecondary)
                }
            }
        )
    }
}
