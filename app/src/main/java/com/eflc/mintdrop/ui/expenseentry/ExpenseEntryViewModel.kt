package com.eflc.mintdrop.ui.expenseentry

import androidx.lifecycle.ViewModel
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val coroutineScope: CoroutineScope
) : ViewModel() {
    private val _state = MutableStateFlow(ExpenseEntryResponse("", 0.0, 0.0))
    val state: StateFlow<ExpenseEntryResponse>
        get() = _state

    fun postExpense(expenseEntryRequest: ExpenseEntryRequest) {
        coroutineScope.launch {
            val expenseEntryResponse : ExpenseEntryResponse = googleSheetsRepository.postExpense(expenseEntryRequest)
            _state.value = expenseEntryResponse
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}