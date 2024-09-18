package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance

@Dao
interface SubcategoryMonthlyBalanceDao {
    @Upsert
    suspend fun saveSubcategoryMonthlyBalance(
        subcategoryMonthlyBalance: SubcategoryMonthlyBalance
    ): Long

    @Query("""
        SELECT * FROM subcategory_monthly_balance
        WHERE subcategory_id = :subcategoryId
        AND year = :year
        AND month = :month
    """)
    suspend fun findBalanceBySubcategoryIdAndPeriod(
        subcategoryId: Long,
        year: Int,
        month: Int
    ): SubcategoryMonthlyBalance?
}
