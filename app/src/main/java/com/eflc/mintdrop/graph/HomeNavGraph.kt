package com.eflc.mintdrop.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.navigation.Graphs
import com.eflc.mintdrop.ui.category.CategoryScreen
import com.eflc.mintdrop.ui.expense.ExpenseScreen
import com.eflc.mintdrop.ui.expenseentry.ExpenseEntryScreen
import com.eflc.mintdrop.ui.income.IncomeScreen

@Composable
fun HomeNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graphs.HOME,
        startDestination = AppScreens.ExpenseScreen.route
    ) {
        composable(route = AppScreens.ExpenseScreen.route) {
            ExpenseScreen(navController)
        }

        composable(route = AppScreens.IncomeScreen.route) {
            IncomeScreen(navController)
        }

        composable(
            route = AppScreens.CategoryScreen.route + "/{category}/{sheet}",
            arguments = listOf(
                navArgument("category") {
                    type = ExpenseCategory
                },
                navArgument("sheet") {
                    type = NavType.StringType
                }
            )
        ) {
            val category = it.arguments?.getParcelable<ExpenseCategory>("category")
            val sheet = it.arguments?.getString("sheet")
            if (category != null && sheet != null) {
                CategoryScreen(navController, category, sheet)
            }
        }

        composable(
            route = AppScreens.ExpenseEntryScreen.route + "/{subcategory}/{sheet}",
            arguments = listOf(
                navArgument("subcategory") {
                    type = ExpenseSubCategory
                },
                navArgument("sheet") {
                    type = NavType.StringType
                }
            )
        ) {
            val subCategory = it.arguments?.getParcelable<ExpenseSubCategory>("subcategory")
            val sheet = it.arguments?.getString("sheet")
            if (subCategory != null && sheet != null) {
                ExpenseEntryScreen(subCategory, sheet)
            }
        }
    }
}
