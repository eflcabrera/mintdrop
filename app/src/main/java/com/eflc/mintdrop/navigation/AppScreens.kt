package com.eflc.mintdrop.navigation

sealed class AppScreens(val route: String) {
    object HomeScreen: AppScreens("home_screen")
    object CategoryScreen: AppScreens("category_screen")
    object ExpenseEntryScreen: AppScreens("expense_entry_screen")
}
