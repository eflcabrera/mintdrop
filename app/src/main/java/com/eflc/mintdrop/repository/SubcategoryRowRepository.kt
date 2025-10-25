package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.SubcategoryRow

interface SubcategoryRowRepository {
    suspend fun saveSubcategoryRow(subcategoryRowEntity: SubcategoryRow): Long
    suspend fun findRowBySubcategoryId(subcategoryId: Long): SubcategoryRow
    suspend fun deleteSubcategoryRow(subcategoryRow: SubcategoryRow)
}
