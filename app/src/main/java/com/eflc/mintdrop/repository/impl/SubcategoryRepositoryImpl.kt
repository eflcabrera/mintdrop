package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.room.dao.SubcategoryDao
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow
import java.time.LocalDateTime
import javax.inject.Inject

class SubcategoryRepositoryImpl @Inject constructor(
    private val dao: SubcategoryDao
) : SubcategoryRepository {
    override suspend fun saveSubcategory(subcategoryEntity: Subcategory): Long {
        return dao.saveSubcategory(subcategoryEntity)
    }

    override suspend fun findSubcategoryById(subcategoryId: Long): Subcategory {
        return dao.getSubcategory(subcategoryId)
    }

    override suspend fun findAllSubcategoriesByCategoryId(categoryId: Long): List<SubcategoryAndSubcategoryRow> {
        return dao.getSubcategoriesByCategoryId(categoryId)
    }

    override suspend fun findSubcategoryWithEntryHistory(subcategoryId: Long): SubcategoryAndEntryHistory {
        return dao.getSubcategoryWithEntryHistory(subcategoryId)
    }

    override suspend fun findSubcategoryByExternalIdAndCategoryType(categoryType: EntryType,externalId: String): Subcategory {
        return dao.getSubcategoryByExternalIdAndCategoryType(categoryType, externalId)
    }

    override suspend fun findLastSubcategoriesUsed(amount: Int): List<SubcategoryAndSubcategoryRow> {
        return dao.getLastXSubcategoriesOrderedByLastEntry(amount)
    }

    override suspend fun deleteSubcategory(subcategory: Subcategory) {
        dao.deleteSubcategory(subcategory)
    }

    override suspend fun updateSubcategoryName(subcategoryId: Long, newName: String) {
        dao.updateSubcategoryName(subcategoryId, newName, LocalDateTime.now().toString())
    }
}
