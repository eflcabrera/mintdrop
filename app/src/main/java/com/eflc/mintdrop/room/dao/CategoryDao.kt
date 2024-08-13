package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory

@Dao
interface CategoryDao {
    @Upsert
    suspend fun saveCategory(category: Category): Long

    @Transaction
    @Query("SELECT * FROM category ORDER BY uid ASC")
    fun getCategories(): List<CategoryAndSubcategory>

    @Transaction
    @Query("SELECT * FROM category WHERE uid = :categoryId")
    fun getCategory(categoryId: Long): CategoryAndSubcategory

    @Transaction
    @Query("SELECT * FROM category WHERE type = :type")
    fun getCategoriesByType(type: EntryType): List<CategoryAndSubcategory>
}