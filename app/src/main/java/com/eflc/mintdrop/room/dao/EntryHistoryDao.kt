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
        WHERE subcategory_id = :subcategoryId AND date > datetime('now', '-30 days')
        ORDER BY date DESC
    """)
    fun getEntryHistoryBySubcategoryIdOrderByDate(subcategoryId: Long): List<EntryHistory>

    @Query("""
        SELECT * from entry_history
        WHERE date > datetime('now', '-30 days')
        ORDER BY created_on DESC
        LIMIT 1
    """)
    fun getTopEntryOrderByDateDesc(): EntryHistory

    @Query("""
        SELECT eh.* FROM entry_history eh
        INNER JOIN shared_expense_entry_detail seed ON eh.uid = seed.entry_record_id
        WHERE seed.settlement_id IS NOT NULL
    """)
    @Transaction
    fun getEntryRecordsWithUnsettledSharedExpenses(): List<EntryRecordAndSharedExpenseDetails>
}
