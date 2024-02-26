package com.eflc.mintdrop.ui.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val googleSheetsRepository: GoogleSheetsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(emptyList<ExpenseCategory>())
    val state: StateFlow<List<ExpenseCategory>>
        get() = _state

    init {
        viewModelScope.launch {
            val response = googleSheetsRepository.getCategories(Constants.GOOGLE_SHEET_ID_2024, Constants.INCOME_SHEET_NAME)
            _state.value = response.categories
        }
    }
}