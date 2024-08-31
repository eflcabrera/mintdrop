package com.eflc.mintdrop.ui.screens.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(emptyList<ExpenseSubCategory>())
    val state: StateFlow<List<ExpenseSubCategory>>
        get() = _state

    init {
        viewModelScope.launch {
            val category = savedStateHandle.get<ExpenseCategory>("category")
            if (category != null) {
                _state.value = category.subCategories
            }
        }
    }
}
