package com.eflc.mintdrop.repository

import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory

interface CategoryRepository {
    suspend fun saveCategory(categoryEntity: Category): Long
    suspend fun findAllCategories(): List<CategoryAndSubcategory>
    suspend fun findCategoryById(id: Long): CategoryAndSubcategory
    suspend fun findCategoriesByType(entryType: EntryType): List<CategoryAndSubcategory>
    suspend fun findCategoryByExternalIdAndEntryType(externalId: String, entryType: EntryType): Category
    suspend fun deleteCategory(category: Category)
    suspend fun updateCategoryName(categoryId: Long, newName: String)
}
