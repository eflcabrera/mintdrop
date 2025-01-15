package com.eflc.mintdrop.service.shared

import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail

interface SharedExpenseService {

    suspend fun createSharedExpenseEntries(sharedEntryRecord: EntryHistory): List<SharedExpenseEntryDetail>
}
