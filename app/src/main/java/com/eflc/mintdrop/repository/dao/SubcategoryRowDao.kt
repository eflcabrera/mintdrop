package com.eflc.mintdrop.repository.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.repository.dao.entity.SubcategoryRow

@Dao
interface SubcategoryRowDao {
    @Upsert
    suspend fun saveSubcategoryRow(subcategoryRow: SubcategoryRow)

    @Query("SELECT * FROM subcategory_row WHERE subcategory_id = :subcategoryId LIMIT 1")
    fun findSubcategoryRowBySubcategoryId(subcategoryId: Int): SubcategoryRow
}