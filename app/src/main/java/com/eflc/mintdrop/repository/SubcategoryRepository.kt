package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow
import kotlinx.coroutines.flow.Flow

interface SubcategoryRepository {
    suspend fun saveSubcategory(subcategoryEntity: Subcategory): Long
    suspend fun findAllSubcategoriesByCategoryId(categoryId: Long): Flow<List<SubcategoryAndSubcategoryRow>>
    suspend fun findSubcategoryWithEntryHistory(subcategoryId: Long): SubcategoryAndEntryHistory
}