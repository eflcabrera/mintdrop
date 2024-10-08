package com.eflc.mintdrop.service.record

import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod

interface EntryRecordService {
    suspend fun createRecord(entryRecord: EntryHistory, sheetName: String, paymentMethod: PaymentMethod?): ExpenseEntryResponse?
    suspend fun deleteRecord(entryRecord: EntryHistory)
}
