package com.eflc.mintdrop.ui.screens.shared

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedExpensesScreen(navComposable: NavController) {
    val sharedExpensesViewModel: SharedExpensesViewModel = hiltViewModel()
}
