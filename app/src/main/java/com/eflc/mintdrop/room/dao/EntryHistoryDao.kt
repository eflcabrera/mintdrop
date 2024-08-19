package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.EntryHistory

@Dao
interface EntryHistoryDao {
    @Upsert
    suspend fun saveEntryHistory(entryHistory: EntryHistory): Long

    @Delete
    suspend fun deleteEntryHistory(entryHistory: EntryHistory)

    @Query("""
        SELECT * FROM entry_history
        WHERE subcategory_id = :subcategoryId AND date BETWEEN datetime('now', '-30 days') AND datetime('now', 'localtime')
        ORDER BY date DESC
    """)
    fun getEntryHistoryBySubcategoryIdOrderByDate(subcategoryId: Long): List<EntryHistory>
}