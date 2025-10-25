package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.room.dao.SubcategoryRowDao
import com.eflc.mintdrop.room.dao.entity.SubcategoryRow
import javax.inject.Inject

class SubcategoryRowRepositoryImpl @Inject constructor(
    private val dao: SubcategoryRowDao
) : SubcategoryRowRepository {
    override suspend fun saveSubcategoryRow(subcategoryRowEntity: SubcategoryRow): Long {
        return dao.saveSubcategoryRow(subcategoryRowEntity)
    }

    override suspend fun findRowBySubcategoryId(subcategoryId: Long): SubcategoryRow {
        return dao.findSubcategoryRowBySubcategoryId(subcategoryId)
    }

    override suspend fun deleteSubcategoryRow(subcategoryRow: SubcategoryRow) {
        dao.deleteSubcategoryRow(subcategoryRow)
    }
}
