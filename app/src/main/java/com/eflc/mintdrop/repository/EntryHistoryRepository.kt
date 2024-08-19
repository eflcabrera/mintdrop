package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.EntryHistory

interface EntryHistoryRepository {
    suspend fun saveEntryHistory(entryHistory: EntryHistory): Long
    suspend fun findEntryHistoryBySubcategoryId(subcategoryId: Long): List<EntryHistory>
}