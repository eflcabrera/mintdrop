package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.room.dao.SubcategoryMonthlyBalanceDao
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance
import javax.inject.Inject

class SubcategoryMonthlyBalanceRepositoryImpl @Inject constructor(
    private val dao: SubcategoryMonthlyBalanceDao
) : SubcategoryMonthlyBalanceRepository {
    override suspend fun saveSubcategoryMonthlyBalance(subcategoryMonthlyBalance: SubcategoryMonthlyBalance): Long {
        return dao.saveSubcategoryMonthlyBalance(subcategoryMonthlyBalance)
    }

    override suspend fun findBalanceBySubcategoryIdAndPeriod(
        subcategoryId: Long,
        year: Int,
        month: Int
    ): SubcategoryMonthlyBalance? {
        return dao.findBalanceBySubcategoryIdAndPeriod(subcategoryId, year, month)
    }

    override suspend fun findCategoryMonthlyBalanceByCategoryIdAndPeriod(
        categoryId: Long,
        year: Int,
        month: Int
    ): List<SubcategoryMonthlyBalance> {
        return dao.getMonthlyBalancesByCategoryIdAndPeriod(categoryId, year, month)
    }
}
