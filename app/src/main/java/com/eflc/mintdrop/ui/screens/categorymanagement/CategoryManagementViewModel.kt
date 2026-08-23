package com.eflc.mintdrop.ui.screens.categorymanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.CategoryManagementResponse
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.service.category.CategoryManagementService
import com.eflc.mintdrop.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val categoryManagementService: CategoryManagementService,
    private val externalSheetRefRepository: ExternalSheetRefRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState: StateFlow<CategoryManagementUiState> = _uiState.asStateFlow()

    fun createCategory(
        categoryName: String,
        entryType: EntryType
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val year = LocalDate.now().year
                val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(year)?.sheetId
                    ?: throw IllegalStateException("No se encontró la planilla para el año $year")
                
                val sheetName = if (entryType == EntryType.EXPENSE) {
                    Constants.EXPENSE_SHEET_NAME
                } else {
                    Constants.INCOME_SHEET_NAME
                }
                
                val categoryId = UUID.randomUUID().toString()
                
                val response = categoryManagementService.createCategory(
                    categoryName = categoryName,
                    categoryId = categoryId,
                    entryType = entryType,
                    spreadsheetId = spreadsheetId,
                    sheetName = sheetName
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastOperation = "Categoría creada: $categoryName",
                    success = response.success
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun createSubcategory(
        categoryId: String,
        subcategoryName: String,
        entryType: EntryType
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val year = LocalDate.now().year
                val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(year)?.sheetId
                    ?: throw IllegalStateException("No se encontró la planilla para el año $year")
                
                val sheetName = if (entryType == EntryType.EXPENSE) {
                    Constants.EXPENSE_SHEET_NAME
                } else {
                    Constants.INCOME_SHEET_NAME
                }
                
                val subcategoryId = UUID.randomUUID().toString()
                
                val response = categoryManagementService.createSubcategory(
                    categoryId = categoryId,
                    subcategoryName = subcategoryName,
                    subcategoryId = subcategoryId,
                    entryType = entryType,
                    spreadsheetId = spreadsheetId,
                    sheetName = sheetName
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastOperation = "Subcategoría creada: $subcategoryName",
                    success = response.success
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun updateCategory(
        categoryId: String,
        newName: String,
        entryType: EntryType
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val year = LocalDate.now().year
                val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(year)?.sheetId
                    ?: throw IllegalStateException("No se encontró la planilla para el año $year")
                
                val sheetName = if (entryType == EntryType.EXPENSE) {
                    Constants.EXPENSE_SHEET_NAME
                } else {
                    Constants.INCOME_SHEET_NAME
                }
                
                val response = categoryManagementService.updateCategory(
                    categoryId = categoryId,
                    newName = newName,
                    entryType = entryType,
                    spreadsheetId = spreadsheetId,
                    sheetName = sheetName
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastOperation = "Categoría actualizada: $newName",
                    success = response.success
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun updateSubcategory(
        subcategoryId: String,
        newName: String,
        entryType: EntryType
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val year = LocalDate.now().year
                val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(year)?.sheetId
                    ?: throw IllegalStateException("No se encontró la planilla para el año $year")
                
                val sheetName = if (entryType == EntryType.EXPENSE) {
                    Constants.EXPENSE_SHEET_NAME
                } else {
                    Constants.INCOME_SHEET_NAME
                }
                
                val response = categoryManagementService.updateSubcategory(
                    subcategoryId = subcategoryId,
                    newName = newName,
                    entryType = entryType,
                    spreadsheetId = spreadsheetId,
                    sheetName = sheetName
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastOperation = "Subcategoría actualizada: $newName",
                    success = response.success
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun deleteCategory(
        categoryId: String,
        entryType: EntryType
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val year = LocalDate.now().year
                val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(year)?.sheetId
                    ?: throw IllegalStateException("No se encontró la planilla para el año $year")
                
                val sheetName = if (entryType == EntryType.EXPENSE) {
                    Constants.EXPENSE_SHEET_NAME
                } else {
                    Constants.INCOME_SHEET_NAME
                }
                
                val response = categoryManagementService.deleteCategory(
                    categoryId = categoryId,
                    entryType = entryType,
                    spreadsheetId = spreadsheetId,
                    sheetName = sheetName
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastOperation = "Categoría eliminada",
                    success = response.success
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun deleteSubcategory(
        subcategoryId: String,
        entryType: EntryType
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val year = LocalDate.now().year
                val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(year)?.sheetId
                    ?: throw IllegalStateException("No se encontró la planilla para el año $year")
                
                val sheetName = if (entryType == EntryType.EXPENSE) {
                    Constants.EXPENSE_SHEET_NAME
                } else {
                    Constants.INCOME_SHEET_NAME
                }
                
                val response = categoryManagementService.deleteSubcategory(
                    subcategoryId = subcategoryId,
                    entryType = entryType,
                    spreadsheetId = spreadsheetId,
                    sheetName = sheetName
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastOperation = "Subcategoría eliminada",
                    success = response.success
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}

data class CategoryManagementUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val lastOperation: String? = null
)

