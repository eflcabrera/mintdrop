package com.eflc.mintdrop.repository

import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow

interface SubcategoryRepository {
    suspend fun saveSubcategory(subcategoryEntity: Subcategory): Long
    suspend fun findSubcategoryById(subcategoryId: Long): Subcategory
    suspend fun findAllSubcategoriesByCategoryId(categoryId: Long): List<SubcategoryAndSubcategoryRow>
    suspend fun findSubcategoryWithEntryHistory(subcategoryId: Long): SubcategoryAndEntryHistory
    suspend fun findSubcategoryByExternalIdAndCategoryType(categoryType: EntryType, externalId: String): Subcategory
    suspend fun findLastSubcategoriesUsed(amount: Int): List<SubcategoryAndSubcategoryRow>
    suspend fun deleteSubcategory(subcategory: Subcategory)
    suspend fun updateSubcategoryName(subcategoryId: Long, newName: String)
}
