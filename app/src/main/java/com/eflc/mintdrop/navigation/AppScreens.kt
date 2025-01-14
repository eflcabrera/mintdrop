package com.eflc.mintdrop.navigation

sealed class AppScreens(val route: String) {
    object ExpenseScreen: AppScreens("expense_screen")
    object IncomeScreen: AppScreens("income_screen")
    object SharedExpensesScreen: AppScreens("shared_expenses_screen")
    object CategoryScreen: AppScreens("category_screen")
    object ExpenseEntryScreen: AppScreens("expense_entry_screen")
}
