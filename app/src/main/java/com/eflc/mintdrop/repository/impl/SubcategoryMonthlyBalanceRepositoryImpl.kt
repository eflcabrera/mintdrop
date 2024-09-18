package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.room.dao.SubcategoryDao
import com.eflc.mintdrop.room.dao.SubcategoryMonthlyBalanceDao
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubcategoryMonthlyBalanceRepositoryImpl @Inject constructor(
    private val dao: SubcategoryMonthlyBalanceDao
) : SubcategoryMonthlyBalanceRepository {
    override suspend fun saveSubcategoryMonthlyBalance(subcategoryMonthlyBalance: SubcategoryMonthlyBalance): Long {
        return withContext(Dispatchers.IO) {
            dao.saveSubcategoryMonthlyBalance(subcategoryMonthlyBalance)
        }
    }
}
