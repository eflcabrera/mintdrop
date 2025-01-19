package com.eflc.mintdrop.service.record

import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails

interface EntryRecordService {
    suspend fun createRecord(entryRecord: EntryHistory, sheetName: String, paymentMethod: PaymentMethod?): ExpenseEntryResponse?
    suspend fun deleteRecord(entryRecord: EntryHistory)
    suspend fun calculateSharedExpenseBalance(pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>): SharedExpenseBalanceData
    suspend fun getPendingSharedExpenses(): List<EntryRecordAndSharedExpenseDetails>
}
