package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun saveCategory(category: Category)

    @Query("SELECT * FROM category ORDER BY id ASC")
    fun getCategories(): Flow<List<Category>>
}