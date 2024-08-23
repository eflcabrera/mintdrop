package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.room.dao.CategoryDao
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.relationship.CategoryAndSubcategory
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {
    override suspend fun saveCategory(categoryEntity: Category): Long {
        return withContext(IO) {
            dao.saveCategory(categoryEntity)
        }
    }

    override suspend fun findAllCategories(): List<CategoryAndSubcategory> {
        return withContext(IO) {
            dao.getCategories()
        }
    }

    override suspend fun findCategoryById(id: Long): CategoryAndSubcategory {
        return withContext(IO) {
            dao.getCategory(id)
        }
    }

    override suspend fun findCategoriesByType(entryType: EntryType): List<CategoryAndSubcategory> {
        return withContext(IO) {
            dao.getCategoriesByType(entryType)
        }
    }
}
