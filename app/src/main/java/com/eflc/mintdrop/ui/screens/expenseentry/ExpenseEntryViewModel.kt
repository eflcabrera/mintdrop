package com.eflc.mintdrop.ui.screens.expenseentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.PaymentMethodRepository
import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance
import com.eflc.mintdrop.service.record.EntryRecordService
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.Constants.MY_USER_ID
import com.eflc.mintdrop.utils.Constants.THEIR_USER_ID
import com.eflc.mintdrop.utils.FormatUtils.Companion.formatDateFromString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

// Estados de error para mejor manejo
sealed class ExpenseEntryError {
    object NetworkError : ExpenseEntryError()
    object DatabaseError : ExpenseEntryError()
    object ValidationError : ExpenseEntryError()
    data class UnknownError(val message: String) : ExpenseEntryError()
}

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val entryHistoryRepository: EntryHistoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val subcategoryMonthlyBalanceRepository: SubcategoryMonthlyBalanceRepository,
    private val entryRecordService: EntryRecordService
) : ViewModel() {
    
    // Cache para evitar consultas repetidas
    private var cachedSubcategory: Pair<EntryType, String>? = null
    private var cachedSubcategoryId: Long? = null
    
    private val _expenseEntryResponse = MutableStateFlow(ExpenseEntryResponse("", 0.0, 0.0))

    private val _entryHistoryList = MutableStateFlow(emptyList<EntryHistory>())
    val entryHistoryList = _entryHistoryList.asStateFlow()

    private val _paymentMethodList = MutableStateFlow(emptyList<PaymentMethod>())
    val paymentMethodList = _paymentMethodList.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _monthlyBalance = MutableStateFlow(SubcategoryMonthlyBalance(0, 0, 0, 0, 0.0))
    val monthlyBalance = _monthlyBalance.asStateFlow()
    
    // Estados de error
    private val _error = MutableStateFlow<ExpenseEntryError?>(null)
    val error = _error.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun postExpense(amount: Double,
                    description: String,
                    sheet: String,
                    isShared: Boolean,
                    expenseSubCategory: ExpenseSubCategory,
                    paymentMethod: PaymentMethod?,
                    selectedDate: String,
                    isPaidByMe: Boolean
    ) {
        viewModelScope.launch {
            try {
                _isSaving.tryEmit(true)
                _error.tryEmit(null) // Limpiar errores anteriores
                
                val categoryType = if (sheet == Constants.EXPENSE_SHEET_NAME) EntryType.EXPENSE else EntryType.INCOME

                val subcategory = getSubcategoryWithCache(categoryType, expenseSubCategory.id)

                // Create new entry
                val entryHistory = EntryHistory(
                    subcategoryId = subcategory.uid,
                    amount = amount,
                    description = description,
                    lastModified = LocalDateTime.now(),
                    isShared = isShared,
                    paymentMethodId = paymentMethod?.uid,
                    date = formatDateFromString(selectedDate)
                        .atTime(LocalTime.now().hour, LocalTime.now().minute, LocalTime.now().second),
                    paidBy = if (isPaidByMe) MY_USER_ID else THEIR_USER_ID,
                    isSettled = if (isShared) false else null
                )

                val expenseEntryResponse: ExpenseEntryResponse? = withContext(IO) {
                    entryRecordService.createRecord(entryHistory, sheet, paymentMethod)
                }

                if (expenseEntryResponse != null) {
                    _expenseEntryResponse.tryEmit(expenseEntryResponse)
                }

                // Actualizar datos en paralelo
                launch { getEntryHistory(categoryType, expenseSubCategory.id) }
                launch { getMonthlyBalance(categoryType, expenseSubCategory.id) }
                
            } catch (e: IOException) {
                // Error de red
                _error.tryEmit(ExpenseEntryError.NetworkError)
            } catch (e: IllegalArgumentException) {
                // Error de validación
                _error.tryEmit(ExpenseEntryError.ValidationError)
            } catch (e: Exception) {
                // Error desconocido
                _error.tryEmit(ExpenseEntryError.UnknownError(e.message ?: "Error desconocido"))
            } finally {
                _isSaving.tryEmit(false)
            }
        }
    }

    fun getEntryHistory(categoryType: EntryType, subCategoryId: String) {
        viewModelScope.launch {
            try {
                _isLoading.tryEmit(true)
                _error.tryEmit(null)
                
                val subcategory = getSubcategoryWithCache(categoryType, subCategoryId)
                val history = withContext(IO) {
                    entryHistoryRepository.findEntryHistoryBySubcategoryId(subcategory.uid)
                }
                _entryHistoryList.tryEmit(history)
                
            } catch (e: IOException) {
                _error.tryEmit(ExpenseEntryError.NetworkError)
            } catch (e: Exception) {
                _error.tryEmit(ExpenseEntryError.DatabaseError)
            } finally {
                _isLoading.tryEmit(false)
            }
        }
    }

    fun getPaymentMethods() {
        viewModelScope.launch {
            try {
                _error.tryEmit(null)
                
                val paymentMethods = withContext(IO) {
                    paymentMethodRepository.findAllPaymentMethods()
                }
                _paymentMethodList.tryEmit(paymentMethods)
                
            } catch (e: IOException) {
                _error.tryEmit(ExpenseEntryError.NetworkError)
            } catch (e: Exception) {
                _error.tryEmit(ExpenseEntryError.DatabaseError)
            }
        }
    }

    fun getMonthlyBalance(categoryType: EntryType, subCategoryId: String) {
        viewModelScope.launch {
            try {
                _error.tryEmit(null)
                
                val currentMonth = LocalDate.now().monthValue
                val currentYear = LocalDate.now().year

                val subcategory = getSubcategoryWithCache(categoryType, subCategoryId)
                
                val balance = withContext(IO) {
                    var balance = subcategoryMonthlyBalanceRepository.findBalanceBySubcategoryIdAndPeriod(
                        subcategory.uid,
                        currentYear,
                        currentMonth
                    )

                    if (balance == null) {
                        val total: Double = entryHistoryRepository.findEntryHistoryBySubcategoryId(subcategory.uid)
                            .filter { it.date.month.value == LocalDate.now().monthValue }
                            .sumOf { it.amount }

                        balance = SubcategoryMonthlyBalance(
                            subcategoryId = subcategory.uid,
                            month = currentMonth,
                            year = currentYear,
                            balance = total
                        )
                        subcategoryMonthlyBalanceRepository.saveSubcategoryMonthlyBalance(balance)
                    }
                    balance
                }

                _monthlyBalance.tryEmit(balance)
                
            } catch (e: IOException) {
                _error.tryEmit(ExpenseEntryError.NetworkError)
            } catch (e: Exception) {
                _error.tryEmit(ExpenseEntryError.DatabaseError)
            }
        }
    }

    /**
     * Función optimizada para obtener subcategoría con cache
     */
    private suspend fun getSubcategoryWithCache(categoryType: EntryType, subCategoryId: String): com.eflc.mintdrop.room.dao.entity.Subcategory {
        val cacheKey = categoryType to subCategoryId
        
        return if (cachedSubcategory == cacheKey && cachedSubcategoryId != null) {
            // Retornar desde cache si existe
            com.eflc.mintdrop.room.dao.entity.Subcategory(
                uid = cachedSubcategoryId!!,
                externalId = subCategoryId,
                name = "", // No necesitamos el nombre para las operaciones
                categoryId = 0L // No necesitamos el categoryId para las operaciones
            )
        } else {
            // Buscar en base de datos y actualizar cache
            val subcategory = withContext(IO) {
                subcategoryRepository.findSubcategoryByExternalIdAndCategoryType(categoryType, subCategoryId)
            }
            cachedSubcategory = cacheKey
            cachedSubcategoryId = subcategory.uid
            subcategory
        }
    }
    
    /**
     * Limpiar errores
     */
    fun clearError() {
        _error.tryEmit(null)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
        // Limpiar cache
        cachedSubcategory = null
        cachedSubcategoryId = null
    }
}
