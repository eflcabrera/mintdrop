package com.eflc.mintdrop.ui.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.utils.Constants
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    navComposable: NavController,
    expenseSubCategory: ExpenseSubCategory
) {
    val expensesViewModel: ExpensesViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var amountInput by remember { mutableStateOf("") }
        var descriptionInput by remember { mutableStateOf("") }
        var expenseSaved by remember { mutableStateOf(false) }

        val amount = amountInput.toDoubleOrNull() ?: 0.0
        val description = descriptionInput
        val expenseEntryResponse by expensesViewModel.state.collectAsState()

        Text(
            text = expenseSubCategory.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        TextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            label = {
                Text("Ingresar gasto")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        TextField(
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
            label = {
                Text("Descripción (opc.)")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        Button(
            onClick = {
                expensesViewModel.postExpense(buildExpenseEntryRequest(
                    row = expenseSubCategory.rowNumber,
                    amount = amount,
                    description = description
                ))
                amountInput = ""
                descriptionInput = ""
                expenseSaved = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(160, 221, 230)),
            enabled = amountInput.isNotBlank() && amountInput.isNotEmpty(),
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Text(text = "Guardar gasto", color = Color.Black)
        }
        if (expenseSaved) {
            Text(
                text = "Monto anterior: ${expenseEntryResponse.previousAmount}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Text(
                text = "Monto final: ${expenseEntryResponse.finalAmount}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }

}

internal fun buildExpenseEntryRequest(
    row: Int,
    amount: Double,
    description: String = "",
    month: Int = LocalDate.now().monthValue
): ExpenseEntryRequest {
    return ExpenseEntryRequest(
        spreadsheetId = Constants.GOOGLE_SHEET_ID_2024,
        sheetName = Constants.EXPENSE_SHEET_NAME,
        month = month,
        amount = amount,
        description = description,
        row = row
    )
}