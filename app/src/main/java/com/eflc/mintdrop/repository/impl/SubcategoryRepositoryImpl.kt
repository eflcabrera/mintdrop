package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.room.dao.SubcategoryDao
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubcategoryRepositoryImpl @Inject constructor(
    private val dao: SubcategoryDao
) : SubcategoryRepository {
    override suspend fun saveSubcategory(subcategoryEntity: Subcategory): Long {
        return withContext(IO) {
            dao.saveSubcategory(subcategoryEntity)
        }
    }

    override suspend fun findSubcategoryById(subcategoryId: Long): Subcategory {
        return withContext(IO) {
            dao.getSubcategory(subcategoryId)
        }
    }

    override suspend fun findAllSubcategoriesByCategoryId(categoryId: Long): List<SubcategoryAndSubcategoryRow> {
        return withContext(IO) {
            dao.getSubcategoriesByCategoryId(categoryId)
        }
    }

    override suspend fun findSubcategoryWithEntryHistory(subcategoryId: Long): SubcategoryAndEntryHistory {
        return withContext(IO) {
            dao.getSubcategoryWithEntryHistory(subcategoryId)
        }
    }

    override suspend fun findSubcategoryByExternalId(externalId: String): Subcategory {
        return withContext(IO) {
            dao.getSubcategoryByExternalId(externalId)
        }
    }

    override suspend fun findLastSubcategoriesUsed(amount: Int): List<SubcategoryAndSubcategoryRow> {
        return withContext(IO) {
            dao.getLastXSubcategoriesOrderedByLastEntry(amount)
        }
    }
}
