package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory

interface CategoryRepository {
    suspend fun saveCategory(categoryEntity: Category): Long
    suspend fun findAllCategories(): List<CategoryAndSubcategory>
    suspend fun findCategoryById(id: Long): CategoryAndSubcategory
}