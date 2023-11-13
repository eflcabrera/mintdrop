package com.eflc.mintdrop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.ui.category.CategoryScreen
import com.eflc.mintdrop.ui.expenses.ExpenseEntryScreen
import com.eflc.mintdrop.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.HomeScreen.route) {
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(
            route = AppScreens.CategoryScreen.route + "/{category}",
            arguments = listOf(
                navArgument("category") {
                    type = ExpenseCategory.NavigationType
                }
            )
        ) {
            val category = it.arguments?.getParcelable<ExpenseCategory>("category")
            if (category != null) {
                CategoryScreen(navController, category)
            }
        }

        composable(
            route = AppScreens.ExpenseEntryScreen.route + "/{subcategory}",
            arguments = listOf(
                navArgument("subcategory") {
                    type = ExpenseSubCategory.NavigationType
                }
            )
        ) {
            val subCategory = it.arguments?.getParcelable<ExpenseSubCategory>("subcategory")
            if (subCategory != null) {
                ExpenseEntryScreen(navController, subCategory)
            }
        }
    }
}