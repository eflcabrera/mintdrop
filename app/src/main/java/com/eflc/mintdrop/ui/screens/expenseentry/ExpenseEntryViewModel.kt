package com.eflc.mintdrop.ui.screens.expenseentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.repository.PaymentMethodRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val coroutineScope: CoroutineScope,
    private val entryHistoryRepository: EntryHistoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {
    private val _expenseEntryResponse = MutableStateFlow(ExpenseEntryResponse("", 0.0, 0.0))

    private val _entryHistoryList = MutableStateFlow(emptyList<EntryHistory>())
    val entryHistoryList = _entryHistoryList.asStateFlow()

    private val _paymentMethodList = MutableStateFlow(emptyList<PaymentMethod>())
    val paymentMethodList = _paymentMethodList.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _monthlyExpense = MutableStateFlow(0.0)
    val monthlyExpense = _monthlyExpense.asStateFlow()

    fun postExpense(amount: Double,
                    description: String,
                    sheet: String,
                    isShared: Boolean,
                    expenseSubCategory: ExpenseSubCategory,
                    paymentMethod: PaymentMethod?
    ) {
        coroutineScope.launch {
            _isSaving.tryEmit(true)
            val subcategory = subcategoryRepository.findSubcategoryByExternalId(expenseSubCategory.id)
            val entryHistory = EntryHistory(
                subcategoryId = subcategory.uid,
                amount = amount,
                description = description,
                lastModified = LocalDateTime.now(),
                isShared = isShared,
                paymentMethodId = paymentMethod?.uid
            )
            entryHistoryRepository.saveEntryHistory(entryHistory)
            subcategory.lastEntryOn = LocalDateTime.now()
            subcategory.lastModified = LocalDateTime.now()
            subcategoryRepository.saveSubcategory(subcategory)

            val expenseEntryResponse : ExpenseEntryResponse = googleSheetsRepository.postExpense(
                buildExpenseEntryRequest(
                    row = expenseSubCategory.rowNumber,
                    amount = amount,
                    description = description,
                    sheet = sheet,
                    isOwedInstallments = false,
                    totalInstallments = 1,
                    paymentMethod = paymentMethod?.description ?: ""
                )
            )
            _expenseEntryResponse.tryEmit(expenseEntryResponse).also {
                getEntryHistory(expenseSubCategory.id)
                getMonthlyExpenses(expenseSubCategory.id)
                _isSaving.tryEmit(false)
            }
        }
    }

    fun getEntryHistory(subCategoryId: String) {
        viewModelScope.launch(IO) {
            val subcategory = subcategoryRepository.findSubcategoryByExternalId(subCategoryId)
            _entryHistoryList.tryEmit(entryHistoryRepository.findEntryHistoryBySubcategoryId(subcategory.uid))
        }
    }

    fun getPaymentMethods() {
        viewModelScope.launch(IO) {
            val paymentMethods = paymentMethodRepository.findAllPaymentMethods()
            _paymentMethodList.tryEmit(paymentMethods)
        }
    }

    fun getMonthlyExpenses(subCategoryId: String) {
        viewModelScope.launch(IO) {
            val subcategory = subcategoryRepository.findSubcategoryByExternalId(subCategoryId)
            val total = entryHistoryRepository.findEntryHistoryBySubcategoryId(subcategory.uid)
                .filter { it.date.month.value == LocalDate.now().monthValue }
                .sumOf { it.amount }
            _monthlyExpense.tryEmit(total)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    private fun buildExpenseEntryRequest(
        row: Int,
        amount: Double,
        description: String = "",
        month: Int = LocalDate.now().monthValue,
        sheet: String,
        isOwedInstallments: Boolean,
        totalInstallments: Int,
        paymentMethod: String
    ): ExpenseEntryRequest {
        return ExpenseEntryRequest(
            spreadsheetId = Constants.GOOGLE_SHEET_ID_2024,
            sheetName = sheet,
            month = month,
            amount = amount,
            description = description,
            row = row,
            isOwedInstallments = isOwedInstallments,
            totalInstallments = totalInstallments,
            paymentMethod = paymentMethod
        )
    }
}
