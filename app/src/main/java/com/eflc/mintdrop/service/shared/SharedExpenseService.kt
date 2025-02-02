package com.eflc.mintdrop.service.shared

import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails

interface SharedExpenseService {

    suspend fun createSharedExpenseEntries(sharedEntryRecord: EntryHistory, entryRecordId: Long): List<SharedExpenseEntryDetail>
    suspend fun deleteSharedExpenseEntries(sharedEntryRecord: EntryHistory)
    suspend fun createBalanceSettlement(balance: Double, pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>): EntryHistory
}
