package com.eflc.mintdrop.ui.expenseentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.room.dao.entity.EntryHistory
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
    private val subcategoryRepository: SubcategoryRepository
) : ViewModel() {
    private val _expenseEntryResponse = MutableStateFlow(ExpenseEntryResponse("", 0.0, 0.0))
    val expenseEntryResponse = _expenseEntryResponse.asStateFlow()

    private val _entryHistoryList = MutableStateFlow(emptyList<EntryHistory>())
    val entryHistoryList = _entryHistoryList.asStateFlow()

    fun postExpense(amount: Double, description: String, sheet: String, expenseSubCategory: ExpenseSubCategory) {
        coroutineScope.launch {
            val subcategory = subcategoryRepository.findSubcategoryByExternalId(expenseSubCategory.id)
            val entryHistory = EntryHistory(
                subcategoryId = subcategory.uid,
                amount = amount,
                description = description,
                lastModified = LocalDateTime.now()
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
                    paymentMethod = ""
                )
            )
            _expenseEntryResponse.tryEmit(expenseEntryResponse)
        }
    }

    fun getEntryHistory(subCategoryId: String) {
        viewModelScope.launch(IO) {
            val subcategory = subcategoryRepository.findSubcategoryByExternalId(subCategoryId)
            _entryHistoryList.tryEmit(entryHistoryRepository.findEntryHistoryBySubcategoryId(subcategory.uid))
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