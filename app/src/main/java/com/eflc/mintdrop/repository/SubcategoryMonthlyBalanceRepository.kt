package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance

interface SubcategoryMonthlyBalanceRepository {
    suspend fun saveSubcategoryMonthlyBalance(
        subcategoryMonthlyBalance: SubcategoryMonthlyBalance
    ): Long

    suspend fun findBalanceBySubcategoryIdAndPeriod(
        subcategoryId: Long, year: Int, month: Int
    ): SubcategoryMonthlyBalance?

    suspend fun findCategoryMonthlyBalanceByCategoryIdAndPeriod(
        categoryId: Long, year: Int, month: Int
    ): List<SubcategoryMonthlyBalance>
}
