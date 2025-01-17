package com.eflc.mintdrop.service.shared

import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail

interface SharedExpenseService {

    suspend fun createSharedExpenseEntries(sharedEntryRecord: EntryHistory, entryRecordId: Long): List<SharedExpenseEntryDetail>
    suspend fun deleteSharedExpenseEntries(sharedEntryRecord: EntryHistory)
}
