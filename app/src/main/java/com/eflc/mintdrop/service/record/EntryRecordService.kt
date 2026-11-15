package com.eflc.mintdrop.service.record

import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.SettleSharedExpensesResult
import com.eflc.mintdrop.models.SettleSyncAggregateState
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import kotlinx.coroutines.flow.Flow

interface EntryRecordService {
    suspend fun createRecord(entryRecord: EntryHistory, sheetName: String, paymentMethod: PaymentMethod?): ExpenseEntryResponse?
    suspend fun deleteRecord(entryRecord: EntryHistory)
    suspend fun calculateSharedExpenseBalance(pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>): SharedExpenseBalanceData
    suspend fun getPendingSharedExpenses(): List<EntryRecordAndSharedExpenseDetails>
    suspend fun settleSharedExpenseBalance(
        balance: Double,
        pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>
    ): SettleSharedExpensesResult
    suspend fun getUnsyncedSettleEntries(): List<EntryHistory>
    fun observeUnsyncedSettleEntries(): Flow<List<EntryHistory>>
    suspend fun getSettleSyncAggregateState(): SettleSyncAggregateState
    suspend fun retryFailedSettleSyncs(): Boolean
    suspend fun retryFailedSync(entryHistoryId: Long): Boolean
}
