package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails

interface EntryHistoryRepository {
    suspend fun saveEntryHistory(entryHistory: EntryHistory): Long
    suspend fun findEntryHistoryById(entryHistoryId: Long): EntryHistory
    suspend fun findEntryHistoryBySubcategoryId(subcategoryId: Long): List<EntryHistory>
    suspend fun findLastEntry(): EntryHistory
    suspend fun deleteEntryHistory(entryHistory: EntryHistory)
    suspend fun getPendingSharedExpenses(): List<EntryRecordAndSharedExpenseDetails>
}
