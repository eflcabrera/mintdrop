package com.eflc.mintdrop.ui.screens.shared

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eflc.mintdrop.models.EntrySyncUiState
import com.eflc.mintdrop.models.SettleSharedExpensesResult
import com.eflc.mintdrop.models.SettleSyncAggregateState
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.repository.PaymentMethodRepository
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import com.eflc.mintdrop.service.record.EntryRecordService
import com.eflc.mintdrop.service.sync.OutboxEnqueuer
import dagger.hilt.android.lifecycle.HiltViewModel
import generateSharedExpensesPdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sharePdfFile
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedExpensesViewModel @Inject constructor(
    private val entryHistoryService: EntryRecordService,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val outboxEnqueuer: OutboxEnqueuer
) : ViewModel() {
    private val _sharedExpenseBalance = MutableStateFlow(SharedExpenseBalanceData(0.0, ArrayList()))
    val sharedExpenseBalanceData = _sharedExpenseBalance.asStateFlow()

    private val _sharedExpenses = MutableStateFlow(listOf<EntryRecordAndSharedExpenseDetails>())
    val sharedExpenses = _sharedExpenses.asStateFlow()

    private val _unsyncedSettleEntries = MutableStateFlow(listOf<EntryHistory>())
    val unsyncedSettleEntries = _unsyncedSettleEntries.asStateFlow()

    private val _settleSyncState = MutableStateFlow(SettleSyncAggregateState.NONE)
    val settleSyncState = _settleSyncState.asStateFlow()

    private val _failedSyncEntryIds = MutableStateFlow(emptySet<Long>())
    val failedSyncEntryIds = _failedSyncEntryIds.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _settleError = MutableStateFlow<String?>(null)
    val settleError = _settleError.asStateFlow()

    private val _pdfFile = MutableStateFlow<File?>(null)
    val pdfFile: StateFlow<File?> = _pdfFile

    private val _pdfError = MutableStateFlow<String?>(null)
    val pdfError: StateFlow<String?> = _pdfError

    private val _pdfMessage = MutableStateFlow<String?>(null)
    val pdfMessage: StateFlow<String?> = _pdfMessage

    private val _paymentMethods = MutableStateFlow(listOf<com.eflc.mintdrop.room.dao.entity.PaymentMethod>())
    val paymentMethods: StateFlow<List<com.eflc.mintdrop.room.dao.entity.PaymentMethod>> = _paymentMethods.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                entryHistoryService.observeUnsyncedSettleEntries(),
                outboxEnqueuer.observeFailedEntryHistoryIds()
            ) { settleEntries, failedIds ->
                settleEntries to failedIds
            }.collect { (settleEntries, failedIds) ->
                _unsyncedSettleEntries.tryEmit(settleEntries)
                _failedSyncEntryIds.tryEmit(failedIds)
                withContext(Dispatchers.IO) {
                    refreshSettleSyncState()
                    if (settleEntries.isEmpty()) {
                        loadPendingSharedExpenses()
                    } else {
                        _sharedExpenses.tryEmit(emptyList())
                        _sharedExpenseBalance.tryEmit(SharedExpenseBalanceData(0.0, ArrayList()))
                    }
                }
            }
        }
    }

    fun getSharedExpenseBalance() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                refreshScreenData()
            }
        }
    }

    fun settleExpenses(balance: Double, sharedExpenses: List<EntryRecordAndSharedExpenseDetails>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isSaving.tryEmit(true)
                _settleError.tryEmit(null)
                when (val result = entryHistoryService.settleSharedExpenseBalance(balance, sharedExpenses)) {
                    is SettleSharedExpensesResult.Success -> refreshScreenData()
                    is SettleSharedExpensesResult.Failure -> {
                        _settleError.tryEmit(result.message)
                        refreshScreenData()
                    }
                }
                _isSaving.tryEmit(false)
            }
        }
    }

    fun retryFailedSettleSyncs() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                entryHistoryService.retryFailedSettleSyncs()
                refreshSettleSyncState()
            }
        }
    }

    fun syncStateFor(entry: EntryHistory): EntrySyncUiState {
        return when {
            entry.syncedToSheets -> EntrySyncUiState.SYNCED
            entry.uid in _failedSyncEntryIds.value -> EntrySyncUiState.FAILED
            else -> EntrySyncUiState.PENDING
        }
    }

    fun isSettleButtonEnabled(currentBalance: Double): Boolean {
        return currentBalance != 0.0 &&
            _unsyncedSettleEntries.value.isEmpty() &&
            _settleSyncState.value == SettleSyncAggregateState.NONE &&
            !_isSaving.value
    }

    fun shouldShowRetryButton(): Boolean {
        return _settleSyncState.value == SettleSyncAggregateState.ALL_FAILED
    }

    fun generateAndSharePdf(context: Context) {
        viewModelScope.launch {
            try {
                _pdfError.value = null
                _pdfMessage.value = null

                val expensesWithDetails = sharedExpenses.value
                val balance = sharedExpenseBalanceData.value.splits
                    .find { it.userId == com.eflc.mintdrop.utils.Constants.MY_USER_ID }
                    ?.let { it.paid - it.owed } ?: 0.0

                val file = generateSharedExpensesPdf(context, expensesWithDetails, balance, paymentMethods.value)
                if (file != null) {
                    _pdfFile.value = file
                    _pdfMessage.value = "PDF generado exitosamente"
                    sharePdfFile(context, file)
                } else {
                    _pdfFile.value = null
                    _pdfError.value = "No se pudo generar el PDF"
                }
            } catch (e: Exception) {
                _pdfFile.value = null
                _pdfError.value = "Error al generar el PDF: ${e.localizedMessage}"
            }
        }
    }

    private suspend fun refreshScreenData() {
        val settleEntries = entryHistoryService.getUnsyncedSettleEntries()
        _unsyncedSettleEntries.tryEmit(settleEntries)
        refreshSettleSyncState()
        if (settleEntries.isEmpty()) {
            loadPendingSharedExpenses()
        } else {
            _sharedExpenses.tryEmit(emptyList())
            _sharedExpenseBalance.tryEmit(SharedExpenseBalanceData(0.0, ArrayList()))
        }
        val paymentMethods = paymentMethodRepository.findAllPaymentMethods()
        _paymentMethods.tryEmit(paymentMethods)
    }

    private suspend fun loadPendingSharedExpenses() {
        val pendingSharedExpenses = entryHistoryService.getPendingSharedExpenses()
        _sharedExpenses.tryEmit(pendingSharedExpenses)
        _sharedExpenseBalance.tryEmit(entryHistoryService.calculateSharedExpenseBalance(pendingSharedExpenses))
    }

    private suspend fun refreshSettleSyncState() {
        _settleSyncState.tryEmit(entryHistoryService.getSettleSyncAggregateState())
    }
}
