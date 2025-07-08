package com.eflc.mintdrop.ui.screens.shared

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.repository.PaymentMethodRepository
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import com.eflc.mintdrop.service.record.EntryRecordService
import dagger.hilt.android.lifecycle.HiltViewModel
import generateSharedExpensesPdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sharePdfFile
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedExpensesViewModel @Inject constructor(
    private val entryHistoryService: EntryRecordService,
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {
    private val _sharedExpenseBalance = MutableStateFlow(SharedExpenseBalanceData(0.0, ArrayList()))
    val sharedExpenseBalanceData = _sharedExpenseBalance.asStateFlow()

    private val _sharedExpenses = MutableStateFlow(listOf<EntryRecordAndSharedExpenseDetails>())
    val sharedExpenses = _sharedExpenses.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _pdfFile = MutableStateFlow<File?>(null)
    val pdfFile: StateFlow<File?> = _pdfFile

    private val _pdfError = MutableStateFlow<String?>(null)
    val pdfError: StateFlow<String?> = _pdfError

    private val _pdfMessage = MutableStateFlow<String?>(null)
    val pdfMessage: StateFlow<String?> = _pdfMessage

    private val _paymentMethods = MutableStateFlow(listOf<com.eflc.mintdrop.room.dao.entity.PaymentMethod>())
    val paymentMethods: StateFlow<List<com.eflc.mintdrop.room.dao.entity.PaymentMethod>> = _paymentMethods.asStateFlow()

    fun getSharedExpenseBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            val pendingSharedExpenses = entryHistoryService.getPendingSharedExpenses()
            _sharedExpenses.tryEmit(pendingSharedExpenses)
            _sharedExpenseBalance.tryEmit(entryHistoryService.calculateSharedExpenseBalance(pendingSharedExpenses))
            
            // Obtener métodos de pago
            val paymentMethods = paymentMethodRepository.findAllPaymentMethods()
            _paymentMethods.tryEmit(paymentMethods)
        }
    }

    fun settleExpenses(balance: Double, sharedExpenses: List<EntryRecordAndSharedExpenseDetails>) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSaving.tryEmit(true)
            entryHistoryService.settleSharedExpenseBalance(balance, sharedExpenses).also {
                getSharedExpenseBalance()
            }
            _isSaving.tryEmit(false)
        }
    }

    fun generateAndSharePdf(context: Context) {
        viewModelScope.launch {
            try {
                _pdfError.value = null
                _pdfMessage.value = null
                
                // Usa tus flows existentes para obtener los datos actuales
                val expensesWithDetails = sharedExpenses.value
                val balance = sharedExpenseBalanceData.value.splits
                    .find { it.userId == com.eflc.mintdrop.utils.Constants.MY_USER_ID }
                    ?.let { it.paid - it.owed } ?: 0.0

                val file = generateSharedExpensesPdf(context, expensesWithDetails, balance, paymentMethods.value)
                if (file != null) {
                    _pdfFile.value = file
                    _pdfMessage.value = "PDF generado exitosamente"
                    sharePdfFile(context, file)
                } else {
                    _pdfFile.value = null
                    _pdfError.value = "No se pudo generar el PDF"
                }
            } catch (e: Exception) {
                _pdfFile.value = null
                _pdfError.value = "Error al generar el PDF: ${e.localizedMessage}"
            }
        }
    }


}
