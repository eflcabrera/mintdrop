package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface SubcategoryDao {
    @Upsert
    suspend fun saveSubcategory(subcategory: Subcategory)

    @Transaction
    @Query("SELECT * FROM subcategory WHERE category_id = :categoryId ORDER BY id ASC")
    fun getSubcategoriesByCategoryId(categoryId: Int): Flow<List<SubcategoryAndSubcategoryRow>>

    @Transaction
    @Query("SELECT * FROM subcategory WHERE id = :subcategoryId")
    fun getSubcategoryWithEntryHistory(subcategoryId: Int): Flow<List<SubcategoryAndEntryHistory>>
}