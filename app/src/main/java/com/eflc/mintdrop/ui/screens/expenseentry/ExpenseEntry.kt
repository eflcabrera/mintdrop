package com.eflc.mintdrop.ui.screens.expenseentry

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.ui.components.EntryDatePicker
import com.eflc.mintdrop.ui.components.LabeledCheckbox
import com.eflc.mintdrop.ui.components.PaymentMethodDropdown
import com.eflc.mintdrop.ui.components.card.EntryHistoryCard
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.FormatUtils
import com.eflc.mintdrop.utils.FormatUtils.Companion.convertMillisToStringDate
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate

// Estado local para evitar recomposiciones innecesarias
data class ExpenseEntryFormState(
    val amountInput: String = "",
    val descriptionInput: String = "",
    val isSharedExpenseInput: Boolean = false,
    val isPaidByMeInput: Boolean = true,
    val paymentMethodInput: PaymentMethod? = null,
    val expenseSaved: Boolean = false,
    val showDatePicker: Boolean = false,
    val showCopiedMessage: Boolean = false,
    val highlightAmount: Boolean = false,
    val highlightDescription: Boolean = false,
    val highlightPaymentMethod: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    expenseSubCategory: ExpenseSubCategory,
    sheet: String
) {
    val expenseEntryViewModel: ExpenseEntryViewModel = hiltViewModel()
    val isExpense = sheet == Constants.EXPENSE_SHEET_NAME
    val categoryType = if (isExpense) EntryType.EXPENSE else EntryType.INCOME
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Estado del formulario memoizado
    var formState by remember { mutableStateOf(ExpenseEntryFormState()) }

    LaunchedEffect(key1 = true, block = {
        expenseEntryViewModel.getEntryHistory(categoryType, expenseSubCategory.id)
        expenseEntryViewModel.getPaymentMethods()
        expenseEntryViewModel.getMonthlyBalance(categoryType, expenseSubCategory.id)
    })

    val history by expenseEntryViewModel.entryHistoryList.collectAsState()
    val paymentMethods by expenseEntryViewModel.paymentMethodList.collectAsState()
    val monthlyBalance by expenseEntryViewModel.monthlyBalance.collectAsState()
    val isSaving by expenseEntryViewModel.isSaving.collectAsState()

    // Valores derivados memoizados para evitar recálculos
    val amount by remember(formState.amountInput) {
        derivedStateOf { formState.amountInput.toDoubleOrNull() ?: 0.0 }
    }

    val description by remember(formState.descriptionInput) {
        derivedStateOf { formState.descriptionInput }
    }

    val isSharedExpense by remember(formState.isSharedExpenseInput) {
        derivedStateOf { formState.isSharedExpenseInput }
    }

    val isPaidByMe by remember(formState.isPaidByMeInput) {
        derivedStateOf { formState.isPaidByMeInput }
    }

    val selectedPaymentMethod by remember(formState.paymentMethodInput) {
        derivedStateOf { formState.paymentMethodInput }
    }

    val datePickerState = rememberDatePickerState()
    val scope = rememberCoroutineScope()

    // Animaciones optimizadas - solo se ejecutan cuando cambian los valores
    val amountHighlightAlpha by animateFloatAsState(
        targetValue = if (formState.highlightAmount) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "amountHighlight"
    )
    val descriptionHighlightAlpha by animateFloatAsState(
        targetValue = if (formState.highlightDescription) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "descriptionHighlight"
    )
    val paymentMethodHighlightAlpha by animateFloatAsState(
        targetValue = if (formState.highlightPaymentMethod) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "paymentMethodHighlight"
    )

    val selectedDate by remember(datePickerState.selectedDateMillis) {
        derivedStateOf {
            datePickerState.selectedDateMillis?.let {
                convertMillisToStringDate(it.plus(1000 * 60 * 60 * 5))
            } ?: convertMillisToStringDate(Instant.now().toEpochMilli())
        }
    }

    val saveButtonLabel by remember(isExpense) {
        derivedStateOf { if (isExpense) "gasto" else "ingreso" }
    }

    val isFormValid by remember(formState.amountInput) {
        derivedStateOf { formState.amountInput.isNotBlank() && formState.amountInput.isNotEmpty() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = expenseSubCategory.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Balance mensual memoizado
            val formattedBalance by remember(monthlyBalance.balance) {
                derivedStateOf { FormatUtils.formatAsCurrency(monthlyBalance.balance) }
            }

            Text(
                text = "Total del mes: $formattedBalance",
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = formState.amountInput,
                onValueChange = { formState = formState.copy(amountInput = it) },
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
                value = formState.descriptionInput,
                onValueChange = { formState = formState.copy(descriptionInput = it) },
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
                    .border(
                        width = (2 * descriptionHighlightAlpha).dp,
                        color = Color(54, 180, 103).copy(alpha = descriptionHighlightAlpha),
                        shape = androidx.compose.material3.MaterialTheme.shapes.small
                    )
            )

            EntryDatePicker(
                formState.showDatePicker,
                datePickerState,
                selectedDate,
                { formState = formState.copy(showDatePicker = !formState.showDatePicker) },
                { formState = formState.copy(showDatePicker = false) }
            )

            if (isExpense) {
                PaymentMethodDropdown(
                    paymentMethods = paymentMethods,
                    onClick = { index ->
                        formState = formState.copy(
                            paymentMethodInput = if (index >= 0) paymentMethods[index] else null
                        )
                    },
                    selectedValue = selectedPaymentMethod,
                    highlightAlpha = paymentMethodHighlightAlpha
                )
                LabeledCheckbox(
                    label = "Es gasto compartido",
                    isChecked = isSharedExpense,
                    onCheckedChange = {
                        formState = formState.copy(isSharedExpenseInput = it)
                    },
                    modifier = Modifier
                        .clickable {
                            formState = formState.copy(isSharedExpenseInput = !formState.isSharedExpenseInput)
                        }
                )
                if (isSharedExpense) {
                    LabeledCheckbox(
                        label = "Pagado por mí",
                        isChecked = isPaidByMe,
                        onCheckedChange = {
                            formState = formState.copy(isPaidByMeInput = it)
                        },
                        modifier = Modifier
                            .clickable {
                                formState = formState.copy(isPaidByMeInput = !formState.isPaidByMeInput)
                            }
                    )
                }
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
                            selectedPaymentMethod,
                            selectedDate,
                            isPaidByMe
                        )
                        // Resetear formulario
                        formState = ExpenseEntryFormState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(160, 221, 230)),
                    enabled = isFormValid,
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Guardar $saveButtonLabel", color = Color.Black)
                }
            }

            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
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

                // Historial optimizado con memoización
                val groupedHistory by remember(history) {
                    derivedStateOf {
                        history.groupBy { it.date.month.value }
                    }
                }

                groupedHistory.forEach { (month, entries) ->
                    if (month != LocalDate.now().monthValue) {
                        Divider(
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                    }
                    entries.forEach { entry ->
                        EntryHistoryCard(
                            modifier = Modifier,
                            entryRecord = entry,
                            paymentMethods = paymentMethods,
                            onLongPress = {
                                // Copiar datos al formulario
                                formState = formState.copy(
                                    amountInput = entry.amount.toString(),
                                    descriptionInput = entry.description,
                                    paymentMethodInput = paymentMethods.find { it.uid == entry.paymentMethodId },
                                    highlightAmount = true,
                                    highlightDescription = true,
                                    highlightPaymentMethod = true
                                )

                                // Scroll hacia arriba
                                scope.launch {
                                    scrollState.animateScrollTo(0)

                                    // Mostrar mensaje de confirmación
                                    snackbarHostState.showSnackbar(
                                        message = "Datos copiados: ${entry.description}",
                                        duration = androidx.compose.material3.SnackbarDuration.Short
                                    )
                                }

                                // Desactivar highlights después de 0.7 segundos
                                scope.launch {
                                    kotlinx.coroutines.delay(700)
                                    formState = formState.copy(
                                        highlightAmount = false,
                                        highlightDescription = false,
                                        highlightPaymentMethod = false
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
