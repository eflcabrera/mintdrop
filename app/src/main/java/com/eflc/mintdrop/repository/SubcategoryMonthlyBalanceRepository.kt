package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance

interface SubcategoryMonthlyBalanceRepository {
    suspend fun saveSubcategoryMonthlyBalance(
        subcategoryMonthlyBalance: SubcategoryMonthlyBalance
    ): Long
}
