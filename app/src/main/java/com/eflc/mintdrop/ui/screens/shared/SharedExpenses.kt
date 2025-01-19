package com.eflc.mintdrop.ui.screens.shared

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eflc.mintdrop.ui.components.card.EntryHistoryCard
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.Constants.MY_USER_ID
import com.eflc.mintdrop.utils.FormatUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedExpensesScreen(navComposable: NavController) {
    val sharedExpensesViewModel: SharedExpensesViewModel = hiltViewModel()

    LaunchedEffect(key1 = true, block = {
        sharedExpensesViewModel.getSharedExpenseBalance()
    })

    val sharedExpenseBalance by sharedExpensesViewModel.sharedExpenseBalanceData.collectAsState()
    val sharedExpenses by sharedExpensesViewModel.sharedExpenses.collectAsState()

    val myUserSplit = sharedExpenseBalance.splits.find { it.userId == MY_USER_ID }
    var currentBalance = 0.0
    myUserSplit?.let {
        currentBalance = myUserSplit.paid.minus(myUserSplit.owed)
    }
    val sheet = Constants.SHARED_EXPENSE_SHEET_NAME

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CenterAlignedTopAppBar(title = { Text(text = sheet) })
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 40.dp, top = 70.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Balance actual: ${FormatUtils.formatAsCurrency(currentBalance)}",
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            sharedExpenses.forEach {
                EntryHistoryCard(Modifier, it.entryRecord, sharedExpenseDetails = it.sharedExpenseDetails)
            }
        }
    }
}
