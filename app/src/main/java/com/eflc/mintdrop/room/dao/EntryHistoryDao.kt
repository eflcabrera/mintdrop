package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails

@Dao
interface EntryHistoryDao {
    @Upsert
    suspend fun saveEntryHistory(entryHistory: EntryHistory): Long

    @Delete
    suspend fun deleteEntryHistory(entryHistory: EntryHistory)

    @Query("SELECT * FROM entry_history WHERE uid = :entryHistoryId")
    suspend fun getEntryHistory(entryHistoryId: Long): EntryHistory

    @Query("""
        SELECT * FROM entry_history
        WHERE subcategory_id = :subcategoryId
        ORDER BY date DESC
        LIMIT :limit
    """)
    fun getEntryHistoryBySubcategoryIdOrderByDate(subcategoryId: Long, limit: Int): List<EntryHistory>

    @Query("""
        SELECT * from entry_history
        WHERE date > datetime('now', '-30 days')
        ORDER BY created_on DESC
        LIMIT 1
    """)
    fun getTopEntryOrderByDateDesc(): EntryHistory

    @Query("SELECT * FROM entry_history WHERE is_settled = 0")
    @Transaction
    fun getEntryRecordsWithUnsettledSharedExpenses(): List<EntryRecordAndSharedExpenseDetails>
    
    @Query("UPDATE entry_history SET synced_to_sheets = 1 WHERE uid = :entryHistoryId")
    suspend fun markAsSyncedToSheets(entryHistoryId: Long)
}
