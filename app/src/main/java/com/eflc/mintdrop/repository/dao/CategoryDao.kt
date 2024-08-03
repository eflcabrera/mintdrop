package com.eflc.mintdrop.repository.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.eflc.mintdrop.repository.dao.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun saveCategory(category: Category)

    @Query("SELECT * FROM category ORDER BY id ASC")
    fun getCategories(): Flow<List<Category>>
}