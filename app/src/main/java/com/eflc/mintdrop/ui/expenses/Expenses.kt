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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        val amount = amountInput.toDoubleOrNull() ?: 0.0
        val description = descriptionInput

        Text(
            text = expenseSubCategory.subCategoryName,
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
                imeAction = ImeAction.Next
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
            },
            enabled = amountInput.isNotBlank() && amountInput.isNotEmpty()
        ) {
            Text("Guardar gasto")
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
        spreadsheetId = Constants.GOOGLE_SHEET_ID_2023,
        sheetName = Constants.EXPENSE_SHEET_NAME,
        month = month,
        amount = amount,
        description = description,
        row = row
    )
}