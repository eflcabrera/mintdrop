package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow

interface SubcategoryRepository {
    suspend fun saveSubcategory(subcategoryEntity: Subcategory): Long
    suspend fun findAllSubcategoriesByCategoryId(categoryId: Long): List<SubcategoryAndSubcategoryRow>
    suspend fun findSubcategoryWithEntryHistory(subcategoryId: Long): SubcategoryAndEntryHistory
}