package com.eflc.mintdrop.service.category

import com.eflc.mintdrop.models.CategoryManagementRequest
import com.eflc.mintdrop.models.CategoryManagementResponse
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.SubcategoryRow

interface CategoryManagementService {
    suspend fun createCategory(
        categoryName: String,
        categoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse

    suspend fun createSubcategory(
        categoryId: String,
        subcategoryName: String,
        subcategoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse

    suspend fun updateCategory(
        categoryId: String,
        newName: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse

    suspend fun updateSubcategory(
        subcategoryId: String,
        newName: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse

    suspend fun deleteCategory(
        categoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse

    suspend fun deleteSubcategory(
        subcategoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse
}

