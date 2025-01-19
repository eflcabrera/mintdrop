package com.eflc.mintdrop.ui.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import com.eflc.mintdrop.service.record.EntryRecordService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedExpensesViewModel @Inject constructor(
    private val entryHistoryService: EntryRecordService
) : ViewModel() {
    private val _sharedExpenseBalance = MutableStateFlow(SharedExpenseBalanceData(0.0, ArrayList()))
    val sharedExpenseBalanceData = _sharedExpenseBalance.asStateFlow()

    private val _sharedExpenses = MutableStateFlow(listOf<EntryRecordAndSharedExpenseDetails>())
    val sharedExpenses = _sharedExpenses.asStateFlow()

    fun getSharedExpenseBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            val pendingSharedExpenses = entryHistoryService.getPendingSharedExpenses()
            _sharedExpenses.tryEmit(pendingSharedExpenses)
            _sharedExpenseBalance.tryEmit(entryHistoryService.calculateSharedExpenseBalance(pendingSharedExpenses))
        }
    }
}
