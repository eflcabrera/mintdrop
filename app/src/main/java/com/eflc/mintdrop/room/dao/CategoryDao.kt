package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun saveCategory(category: Category): Long

    @Transaction
    @Query("SELECT * FROM category ORDER BY uid ASC")
    fun getCategories(): Flow<List<CategoryAndSubcategory>>

    @Transaction
    @Query("SELECT * FROM category WHERE uid = :categoryId")
    fun getCategory(categoryId: Long): CategoryAndSubcategory
}