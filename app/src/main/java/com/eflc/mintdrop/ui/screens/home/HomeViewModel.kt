package com.eflc.mintdrop.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.EntryData
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val subcategoryRowRepository: SubcategoryRowRepository,
    private val entryHistoryRepository: EntryHistoryRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _lastEntryData = MutableStateFlow(EntryData(0, "", "", 0.0))
    val lastEntryData = _lastEntryData.asStateFlow()

    fun getLastEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            entryHistoryRepository.findLastEntry().let {
                val subcategory = subcategoryRepository.findSubcategoryById(it.subcategoryId)
                _lastEntryData.tryEmit(
                    EntryData(
                        id = it.uid,
                        description = it.description,
                        categoryName = subcategory.name,
                        amount = it.amount
                    )
                )
            }
        }
    }

    fun deleteEntry() {
        viewModelScope.launch(Dispatchers.IO) {
            _lastEntryData.let {
                entryHistoryRepository.findEntryHistoryById(it.value.id).let {
                    entryHistory ->
                        run {
                            entryHistoryRepository.deleteEntryHistory(entryHistory)
                            val subcategory = subcategoryRepository.findSubcategoryById(entryHistory.subcategoryId)
                            val row = subcategoryRowRepository.findRowBySubcategoryId(subcategory.uid)
                            val c = categoryRepository.findCategoryById(subcategory.categoryId)
                            googleSheetsRepository.postExpense(
                                buildExpenseEntryRequest(
                                    row = row.rowNumber,
                                    amount = -1 * entryHistory.amount,
                                    description = "UNDO ${entryHistory.description}",
                                    sheet = if (c.category.type == EntryType.EXPENSE) Constants.EXPENSE_SHEET_NAME else Constants.INCOME_SHEET_NAME,
                                    isOwedInstallments = false,
                                    totalInstallments = 1,
                                    paymentMethod = ""
                                )
                            )
                        }
                }
            }
        }
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
