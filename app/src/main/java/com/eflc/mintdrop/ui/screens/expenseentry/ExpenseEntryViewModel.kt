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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val entryHistoryRepository: EntryHistoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val subcategoryMonthlyBalanceRepository: SubcategoryMonthlyBalanceRepository,
    private val entryRecordService: EntryRecordService
) : ViewModel() {
    private val _expenseEntryResponse = MutableStateFlow(ExpenseEntryResponse("", 0.0, 0.0))

    private val _entryHistoryList = MutableStateFlow(emptyList<EntryHistory>())
    val entryHistoryList = _entryHistoryList.asStateFlow()

    private val _paymentMethodList = MutableStateFlow(emptyList<PaymentMethod>())
    val paymentMethodList = _paymentMethodList.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _monthlyBalance = MutableStateFlow(SubcategoryMonthlyBalance(0, 0, 0, 0, 0.0))
    val monthlyBalance = _monthlyBalance.asStateFlow()

    fun postExpense(amount: Double,
                    description: String,
                    sheet: String,
                    isShared: Boolean,
                    expenseSubCategory: ExpenseSubCategory,
                    paymentMethod: PaymentMethod?,
                    selectedDate: String,
                    isPaidByMe: Boolean
    ) {
        coroutineScope.launch {
            _isSaving.tryEmit(true)
            val categoryType = if (sheet == Constants.EXPENSE_SHEET_NAME) EntryType.EXPENSE else EntryType.INCOME

            val subcategory = subcategoryRepository.findSubcategoryByExternalIdAndCategoryType(categoryType, expenseSubCategory.id)

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

            val expenseEntryResponse : ExpenseEntryResponse? = entryRecordService.createRecord(entryHistory, sheet, paymentMethod)

            if (expenseEntryResponse != null) {
                _expenseEntryResponse.tryEmit(expenseEntryResponse)
            }

            getEntryHistory(categoryType, expenseSubCategory.id)
            getMonthlyBalance(categoryType, expenseSubCategory.id)
            _isSaving.tryEmit(false)
        }
    }

    fun getEntryHistory(categoryType: EntryType, subCategoryId: String) {
        viewModelScope.launch(IO) {
            val subcategory = subcategoryRepository.findSubcategoryByExternalIdAndCategoryType(categoryType, subCategoryId)
            _entryHistoryList.tryEmit(entryHistoryRepository.findEntryHistoryBySubcategoryId(subcategory.uid))
        }
    }

    fun getPaymentMethods() {
        viewModelScope.launch(IO) {
            val paymentMethods = paymentMethodRepository.findAllPaymentMethods()
            _paymentMethodList.tryEmit(paymentMethods)
        }
    }

    fun getMonthlyBalance(categoryType: EntryType, subCategoryId: String) {
        viewModelScope.launch(IO) {
            val currentMonth = LocalDate.now().monthValue
            val currentYear = LocalDate.now().year

            val subcategory = subcategoryRepository.findSubcategoryByExternalIdAndCategoryType(categoryType, subCategoryId)
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

            _monthlyBalance.tryEmit(balance)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}
