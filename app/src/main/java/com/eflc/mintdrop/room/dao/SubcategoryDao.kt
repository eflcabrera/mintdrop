package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow

@Dao
interface SubcategoryDao {
    @Upsert
    suspend fun saveSubcategory(subcategory: Subcategory): Long

    @Transaction
    @Query("SELECT * FROM subcategory WHERE category_id = :categoryId ORDER BY uid ASC")
    fun getSubcategoriesByCategoryId(categoryId: Long): List<SubcategoryAndSubcategoryRow>

    @Transaction
    @Query("SELECT * FROM subcategory WHERE uid = :subcategoryId")
    fun getSubcategoryWithEntryHistory(subcategoryId: Long): SubcategoryAndEntryHistory
}