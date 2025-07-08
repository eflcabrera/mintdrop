package com.eflc.mintdrop.ui.screens.shared

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eflc.mintdrop.ui.components.card.EntryHistoryCard
import com.eflc.mintdrop.ui.components.dialog.ConfirmationEntryDialog
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.Constants.MY_USER_ID
import com.eflc.mintdrop.utils.FormatUtils
import kotlin.math.absoluteValue

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
    val isSaving by sharedExpensesViewModel.isSaving.collectAsState()
    val shouldShowSettlementDialog = remember { mutableStateOf(false) }
    val pdfError by sharedExpensesViewModel.pdfError.collectAsState()
    val pdfMessage by sharedExpensesViewModel.pdfMessage.collectAsState()
    val context = LocalContext.current

    val myUserSplit = sharedExpenseBalance.splits.find { it.userId == MY_USER_ID }
    var currentBalance = 0.0
    myUserSplit?.let {
        currentBalance = myUserSplit.paid.minus(myUserSplit.owed)
    }
    val sheet = Constants.SHARED_EXPENSE_SHEET_NAME
    val resultOperationCaption = if (currentBalance < 0.0) "debitarán" else "acreditarán"

    if (shouldShowSettlementDialog.value) {
        ConfirmationEntryDialog(
            onDismissRequest = { },
            onConfirmation = { sharedExpensesViewModel.settleExpenses(currentBalance, sharedExpenses) },
            dialogTitle = "Saldar cuentas",
            dialogText = "¿Estás seguro de querer saldar estos gastos?\nSe te $resultOperationCaption ${FormatUtils.formatAsCurrency(currentBalance.absoluteValue)}",
            isVisible = shouldShowSettlementDialog,
            confirmLabel = "Saldar"
        )
    }

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
                    horizontalAlignment = Alignment.CenterHorizontally,
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
                    Button(
                        onClick = {
                            shouldShowSettlementDialog.value = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(160, 221, 230)),
                        enabled = currentBalance != 0.0,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "Saldar cuentas", color = Color.Black)
                    }
                    
                    // Botón para generar y compartir PDF
                    Button(
                        onClick = {
                            sharedExpensesViewModel.generateAndSharePdf(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(54, 180, 103)),
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "Generar y Compartir PDF", color = Color.Black)
                    }

                    pdfError?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                    
                    pdfMessage?.let { message ->
                        Text(
                            text = message,
                            color = Color.Green,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp, top = 270.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(35.dp)
                        .padding(bottom = 14.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                sharedExpenses.forEach {
                    EntryHistoryCard(Modifier, it.entryRecord, sharedExpenseDetails = it.sharedExpenseDetails)
                }
            }
        }
    }
}
