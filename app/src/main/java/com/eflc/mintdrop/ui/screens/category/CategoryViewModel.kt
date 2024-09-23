package com.eflc.mintdrop.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val subcategoryMonthlyBalanceRepository: SubcategoryMonthlyBalanceRepository
) : ViewModel() {
    private val _state = MutableStateFlow(emptyList<ExpenseSubCategory>())
    val state: StateFlow<List<ExpenseSubCategory>>
        get() = _state

    private val _monthlyBalance = MutableStateFlow(0.0)
    val monthlyBalance = _monthlyBalance.asStateFlow()

    init {
        viewModelScope.launch {
            val category = savedStateHandle.get<ExpenseCategory>("category")
            if (category != null) {
                _state.value = category.subCategories
            }
        }
    }

    fun getMonthlyBalance(categoryId: String, entryType: EntryType) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentMonth = LocalDate.now().monthValue
            val currentYear = LocalDate.now().year
            val category = categoryRepository.findCategoryByExternalIdAndEntryType(categoryId, entryType)

            val totalBalance: Double = subcategoryMonthlyBalanceRepository.findCategoryMonthlyBalanceByCategoryIdAndPeriod(
                category.uid,
                currentYear,
                currentMonth
            ).sumOf { it.balance }

            _monthlyBalance.tryEmit(totalBalance)
        }
    }
}
