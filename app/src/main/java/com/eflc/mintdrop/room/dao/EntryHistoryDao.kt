package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryHistoryDao {
    @Upsert
    suspend fun saveEntryHistory(entryHistory: EntryHistory)

    @Delete
    suspend fun deleteEntryHistory(entryHistory: EntryHistory)

    @Query("SELECT * FROM entry_history WHERE subcategory_id = :subcategoryId ORDER BY date DESC")
    fun getEntryHistoryBySubcategoryIdOrderByDate(subcategoryId: Int): Flow<List<EntryHistory>>
}