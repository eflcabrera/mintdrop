package com.eflc.mintdrop.ui.expenses

import androidx.lifecycle.ViewModel
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val coroutineScope: CoroutineScope
) : ViewModel() {
    fun postExpense(expenseEntryRequest: ExpenseEntryRequest) {
        coroutineScope.launch {
            googleSheetsRepository.postExpense(expenseEntryRequest)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}