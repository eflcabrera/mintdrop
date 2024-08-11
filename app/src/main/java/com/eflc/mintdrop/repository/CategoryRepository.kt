package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun saveCategory(categoryEntity: Category): Long
    suspend fun findAllCategories(): Flow<List<CategoryAndSubcategory>>
    suspend fun findCategoryById(id: Long): CategoryAndSubcategory
}