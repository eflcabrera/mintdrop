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

    override suspend fun findEntryHistoryById(entryHistoryId: Long): EntryHistory {
        return withContext(IO) {
            dao.getEntryHistory(entryHistoryId)
        }
    }

    override suspend fun findEntryHistoryBySubcategoryId(subcategoryId: Long): List<EntryHistory> {
        return withContext(IO) {
            dao.getEntryHistoryBySubcategoryIdOrderByDate(subcategoryId)
        }
    }

    override suspend fun findLastEntry(): EntryHistory {
        return withContext(IO) {
            dao.getTopEntryOrderByDateDesc()
        }
    }

    override suspend fun deleteEntryHistory(entryHistory: EntryHistory) {
        return withContext(IO) {
            dao.deleteEntryHistory(entryHistory)
        }
    }
}
