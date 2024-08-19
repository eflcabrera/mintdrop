package com.eflc.mintdrop.ui.expenseentry

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
import androidx.compose.runtime.LaunchedEffect
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
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.ui.card.EntryHistoryCard
import com.eflc.mintdrop.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    expenseSubCategory: ExpenseSubCategory,
    sheet: String
) {
    val expenseEntryViewModel: ExpenseEntryViewModel = hiltViewModel()

    LaunchedEffect(key1 = true, block = {
        expenseEntryViewModel.getEntryHistory(expenseSubCategory.id)
    })

    val history by expenseEntryViewModel.entryHistoryList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var amountInput by remember { mutableStateOf("") }
        var descriptionInput by remember { mutableStateOf("") }
        var expenseSaved by remember { mutableStateOf(false) }

        val amount = amountInput.toDoubleOrNull() ?: 0.0
        val description = descriptionInput
        val expenseEntryResponse by expenseEntryViewModel.expenseEntryResponse.collectAsState()

        val saveButtonLabel = if (sheet == Constants.EXPENSE_SHEET_NAME) "gasto" else "ingreso"

        Text(
            text = expenseSubCategory.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        TextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            label = {
                Text("Ingresar $saveButtonLabel")
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
                expenseEntryViewModel.postExpense(amount, description, sheet, expenseSubCategory)
                amountInput = ""
                descriptionInput = ""
                expenseSaved = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(160, 221, 230)),
            enabled = amountInput.isNotBlank() && amountInput.isNotEmpty(),
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Text(text = "Guardar $saveButtonLabel", color = Color.Black)
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
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            history.forEach { entry: EntryHistory ->
                EntryHistoryCard(Modifier, entry)
            }
        }
    }
}