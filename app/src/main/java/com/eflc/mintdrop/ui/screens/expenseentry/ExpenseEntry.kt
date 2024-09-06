package com.eflc.mintdrop.ui.screens.expenseentry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.ui.components.LabeledCheckbox
import com.eflc.mintdrop.ui.components.PaymentMethodDropdown
import com.eflc.mintdrop.ui.components.card.EntryHistoryCard
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
        expenseEntryViewModel.getPaymentMethods()
    })

    val history by expenseEntryViewModel.entryHistoryList.collectAsState()
    val paymentMethods by expenseEntryViewModel.paymentMethodList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        var amountInput by remember { mutableStateOf("") }
        var descriptionInput by remember { mutableStateOf("") }
        var isSharedExpenseInput by remember { mutableStateOf(false) }
        var paymentMethodInput by remember { mutableStateOf<PaymentMethod?>(null) }
        var expenseSaved by remember { mutableStateOf(false) }

        val amount = amountInput.toDoubleOrNull() ?: 0.0
        val description = descriptionInput
        val isSharedExpense = isSharedExpenseInput
        val selectedPaymentMethod = paymentMethodInput

        val isSaving by expenseEntryViewModel.isSaving.collectAsState()

        val isExpense = sheet == Constants.EXPENSE_SHEET_NAME

        val saveButtonLabel = if (isExpense) "gasto" else "ingreso"

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

        if (isExpense) {
            PaymentMethodDropdown(
                paymentMethods = paymentMethods,
                onClick = {
                    index -> paymentMethodInput = if (index >= 0) paymentMethods[index] else null
                },
                selectedValue = selectedPaymentMethod
            )
            LabeledCheckbox(
                label = "Es gasto compartido",
                isChecked = isSharedExpenseInput,
                onCheckedChange = { isSharedExpenseInput = it },
                modifier = Modifier
                    .clickable { isSharedExpenseInput = !isSharedExpenseInput }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    expenseEntryViewModel.postExpense(
                        amount,
                        description,
                        sheet,
                        isSharedExpense,
                        expenseSubCategory,
                        selectedPaymentMethod
                    )
                    amountInput = ""
                    descriptionInput = ""
                    isSharedExpenseInput = false
                    expenseSaved = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(160, 221, 230)),
                enabled = amountInput.isNotBlank() && amountInput.isNotEmpty(),
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .height(50.dp)
            ) {
                Text(text = "Guardar $saveButtonLabel", color = Color.Black)
            }
        }
        /*
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
        */
        Text(
            text = "Últimas entradas",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
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
            }
            history.forEach { entry: EntryHistory ->
                EntryHistoryCard(Modifier, entry, paymentMethods)
            }
        }
    }
}
