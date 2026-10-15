package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.CatAmber
import com.example.ui.theme.CatBlue
import com.example.ui.theme.CatCyan
import com.example.ui.theme.CatEmerald
import com.example.ui.theme.CatIndigo
import com.example.ui.theme.CatOrange
import com.example.ui.theme.CatPink
import com.example.ui.theme.CatPurple
import com.example.ui.theme.CatRose
import com.example.ui.theme.CatTeal
import com.example.ui.theme.PennyCoralExpense
import com.example.ui.theme.PennyGreenPrimary

data class CategoryItem(
    val name: String,
    val isExpense: Boolean,
    val icon: ImageVector,
    val color: Color
)

object CategoryRegistry {
    val expenseCategories = listOf(
        CategoryItem("Groceries", true, Icons.Default.ShoppingCart, CatAmber),
        CategoryItem("Dining", true, Icons.Default.Fastfood, CatOrange),
        CategoryItem("Shopping", true, Icons.Default.LocalMall, CatPink),
        CategoryItem("Housing", true, Icons.Default.Home, CatIndigo),
        CategoryItem("Transport", true, Icons.Default.DirectionsCar, CatCyan),
        CategoryItem("Entertainment", true, Icons.Default.Movie, CatPurple),
        CategoryItem("Health", true, Icons.Default.FitnessCenter, CatEmerald),
        CategoryItem("Utilities", true, Icons.Default.Receipt, CatTeal),
        CategoryItem("Education", true, Icons.Default.School, CatBlue),
        CategoryItem("Medical", true, Icons.Default.MedicalServices, PennyCoralExpense),
        CategoryItem("Other Expense", true, Icons.Default.Workspaces, CatRose)
    )

    val incomeCategories = listOf(
        CategoryItem("Salary", false, Icons.Default.Work, PennyGreenPrimary),
        CategoryItem("Freelance", false, Icons.Default.LaptopMac, CatTeal),
        CategoryItem("Investment", false, Icons.Default.TrendingUp, CatEmerald),
        CategoryItem("Gift", false, Icons.Default.CardGiftcard, CatPink),
        CategoryItem("Rental", false, Icons.Default.AccountBalance, CatIndigo),
        CategoryItem("Other Income", false, Icons.Default.AttachMoney, CatAmber)
    )

    val allCategories = expenseCategories + incomeCategories

    fun getCategory(name: String, isExpense: Boolean = true): CategoryItem {
        return allCategories.find { it.name.equals(name, ignoreCase = true) }
            ?: (if (isExpense) expenseCategories.last() else incomeCategories.last())
    }
}
