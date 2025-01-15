package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail

@Dao
interface SharedExpenseEntryDetailDao {

    @Upsert
    suspend fun saveSharedExpenseEntryDetail(sharedExpenseEntryDetail: SharedExpenseEntryDetail): Long
}
