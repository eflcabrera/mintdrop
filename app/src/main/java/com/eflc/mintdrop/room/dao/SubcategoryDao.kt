package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.Subcategory
import kotlinx.coroutines.flow.Flow

@Dao
interface SubcategoryDao {
    @Upsert
    suspend fun saveSubcategory(subcategory: Subcategory)

    @Query("SELECT * FROM subcategory WHERE category_id = :categoryId ORDER BY id ASC")
    fun getSubcategoriesByCategoryId(categoryId: Int): Flow<List<Subcategory>>
}