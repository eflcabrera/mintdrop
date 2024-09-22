package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.room.dao.EntryHistoryDao
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import javax.inject.Inject

class EntryHistoryRepositoryImpl @Inject constructor(
    private val dao: EntryHistoryDao
) : EntryHistoryRepository {
    override suspend fun saveEntryHistory(entryHistory: EntryHistory): Long {
        return dao.saveEntryHistory(entryHistory)
    }

    override suspend fun findEntryHistoryById(entryHistoryId: Long): EntryHistory {
        return dao.getEntryHistory(entryHistoryId)
    }

    override suspend fun findEntryHistoryBySubcategoryId(subcategoryId: Long): List<EntryHistory> {
        return dao.getEntryHistoryBySubcategoryIdOrderByDate(subcategoryId)
    }

    override suspend fun findLastEntry(): EntryHistory {
        return dao.getTopEntryOrderByDateDesc()
    }

    override suspend fun deleteEntryHistory(entryHistory: EntryHistory) {
        return dao.deleteEntryHistory(entryHistory)
    }
}
