package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail

@Dao
interface SharedExpenseEntryDetailDao {

    @Upsert
    suspend fun saveSharedExpenseEntryDetail(sharedExpenseEntryDetail: SharedExpenseEntryDetail): Long

    @Query("DELETE FROM shared_expense_entry_detail WHERE entry_record_id = :entryRecordId")
    suspend fun deleteAllByEntryRecordId(entryRecordId: Long)

}
