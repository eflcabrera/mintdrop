package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.SharedExpenseSettlement

@Dao
interface SharedExpenseSettlementDao {
    @Upsert
    suspend fun saveSharedExpenseSettlement(sharedExpenseSettlement: SharedExpenseSettlement): Long
}
