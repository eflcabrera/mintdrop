package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("""
        SELECT smb.* FROM subcategory_monthly_balance smb
        INNER JOIN subcategory sc ON sc.uid = smb.subcategory_id
        INNER JOIN category c ON c.uid = sc.category_id
        WHERE c.uid = :categoryId
        AND year = :year
        AND month = :month
    """)
    @Transaction
    suspend fun getMonthlyBalancesByCategoryIdAndPeriod(
        categoryId: Long,
        year: Int,
        month: Int
    ): List<SubcategoryMonthlyBalance>
}
