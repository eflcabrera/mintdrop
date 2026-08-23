package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.room.dao.CategoryDao
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory
import java.time.LocalDateTime
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {
    override suspend fun saveCategory(categoryEntity: Category): Long {
        return dao.saveCategory(categoryEntity)
    }

    override suspend fun findAllCategories(): List<CategoryAndSubcategory> {
        return dao.getCategories()
    }

    override suspend fun findCategoryById(id: Long): CategoryAndSubcategory {
        return dao.getCategory(id)
    }

    override suspend fun findCategoriesByType(entryType: EntryType): List<CategoryAndSubcategory> {
        return dao.getCategoriesByType(entryType)
    }

    override suspend fun findCategoryByExternalIdAndEntryType(externalId: String, entryType: EntryType): Category {
        return dao.getCategoryByExternalIdAndEntryType(externalId, entryType)
    }

    override suspend fun deleteCategory(category: Category) {
        dao.deleteCategory(category)
    }

    override suspend fun updateCategoryName(categoryId: Long, newName: String) {
        dao.updateCategoryName(categoryId, newName, LocalDateTime.now().toString())
    }
}
