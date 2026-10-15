package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.CategoryBreakdownScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MonthlyReportScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionListScreen
import com.example.ui.theme.CharcoalTextPrimary
import com.example.ui.theme.PennyGreenPrimary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceSubtle
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.FinanceViewModel

sealed class AppDestination(val route: String) {
    object Home : AppDestination("home")
    object Transactions : AppDestination("transactions")
    object AddTransaction : AppDestination("add_transaction")
    object CategoryBreakdown : AppDestination("category_breakdown")
    object MonthlyReport : AppDestination("monthly_report")
    object Budgets : AppDestination("budgets")
    object Settings : AppDestination("settings")
}

data class NavItem(
    val title: String,
    val destination: AppDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun PennyWiseApp(
    viewModel: FinanceViewModel = viewModel()
) {
    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }
    var backStack by remember { mutableStateOf(listOf<AppDestination>(AppDestination.Home)) }

    fun navigateTo(dest: AppDestination) {
        if (currentDestination != dest) {
            backStack = backStack + dest
            currentDestination = dest
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            val updated = backStack.dropLast(1)
            backStack = updated
            currentDestination = updated.last()
        } else {
            currentDestination = AppDestination.Home
        }
    }

    // Handle system back button
    BackHandler(enabled = currentDestination != AppDestination.Home) {
        navigateBack()
    }

    val navItems = listOf(
        NavItem("Home", AppDestination.Home, Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("Transactions", AppDestination.Transactions, Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
        NavItem("Breakdown", AppDestination.CategoryBreakdown, Icons.Filled.PieChart, Icons.Outlined.PieChart),
        NavItem("Report", AppDestination.MonthlyReport, Icons.Filled.Assessment, Icons.Outlined.Assessment),
        NavItem("Budgets", AppDestination.Budgets, Icons.Filled.Savings, Icons.Outlined.Savings)
    )

    val showBottomBar = currentDestination != AppDestination.AddTransaction &&
            currentDestination != AppDestination.Settings

    Scaffold(
        containerColor = SlateBackground,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = SlateSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    navItems.forEach { item ->
                        val isSelected = when (item.destination) {
                            AppDestination.Home -> currentDestination == AppDestination.Home
                            AppDestination.Transactions -> currentDestination == AppDestination.Transactions
                            AppDestination.CategoryBreakdown -> currentDestination == AppDestination.CategoryBreakdown
                            AppDestination.MonthlyReport -> currentDestination == AppDestination.MonthlyReport
                            AppDestination.Budgets -> currentDestination == AppDestination.Budgets
                            else -> false
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigateTo(item.destination) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PennyGreenPrimary,
                                selectedTextColor = PennyGreenPrimary,
                                unselectedIconColor = SlateTextSecondary,
                                unselectedTextColor = SlateTextSecondary,
                                indicatorColor = PennyGreenPrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.Home -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToAddTransaction = { navigateTo(AppDestination.AddTransaction) },
                        onNavigateToTransactions = { navigateTo(AppDestination.Transactions) },
                        onNavigateToCategoryBreakdown = { navigateTo(AppDestination.CategoryBreakdown) },
                        onNavigateToSettings = { navigateTo(AppDestination.Settings) }
                    )
                }

                AppDestination.Transactions -> {
                    TransactionListScreen(
                        viewModel = viewModel,
                        onNavigateToAddTransaction = { navigateTo(AppDestination.AddTransaction) }
                    )
                }

                AppDestination.AddTransaction -> {
                    AddTransactionScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                AppDestination.CategoryBreakdown -> {
                    CategoryBreakdownScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                AppDestination.MonthlyReport -> {
                    MonthlyReportScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }

                AppDestination.Budgets -> {
                    BudgetScreen(
                        viewModel = viewModel
                    )
                }

                AppDestination.Settings -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }
            }
        }
    }
}
