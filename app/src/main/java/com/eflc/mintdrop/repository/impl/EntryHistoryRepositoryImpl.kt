package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.room.dao.EntryHistoryDao
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EntryHistoryRepositoryImpl @Inject constructor(
    private val dao: EntryHistoryDao
) : EntryHistoryRepository {
    override suspend fun saveEntryHistory(entryHistory: EntryHistory): Long {
        return withContext(IO) {
            dao.saveEntryHistory(entryHistory)
        }
    }

    override suspend fun findEntryHistoryBySubcategoryId(subcategoryId: Long): List<EntryHistory> {
        return withContext(IO) {
            dao.getEntryHistoryBySubcategoryIdOrderByDate(subcategoryId)
        }
    }
}
