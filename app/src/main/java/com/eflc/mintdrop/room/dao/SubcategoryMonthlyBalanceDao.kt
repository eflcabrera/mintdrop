package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance

@Dao
interface SubcategoryMonthlyBalanceDao {
    @Upsert
    suspend fun saveSubcategoryMonthlyBalance(
        subcategoryMonthlyBalance: SubcategoryMonthlyBalance
    ): Long
}
