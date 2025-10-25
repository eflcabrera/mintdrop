package com.eflc.mintdrop.service.category.impl

import androidx.room.withTransaction
import com.eflc.mintdrop.models.CategoryManagementRequest
import com.eflc.mintdrop.models.CategoryManagementResponse
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.SubcategoryRow
import com.eflc.mintdrop.service.category.CategoryManagementService
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class CategoryManagementServiceImpl @Inject constructor(
    private val db: JulepDatabase,
    private val categoryRepository: CategoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val subcategoryRowRepository: SubcategoryRowRepository,
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val externalSheetRefRepository: ExternalSheetRefRepository
) : CategoryManagementService {

    override suspend fun createCategory(
        categoryName: String,
        categoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse {
        return try {
            // Crear categoría en Google Sheets
            val request = CategoryManagementRequest(
                action = "createCategory",
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                categoryId = categoryId,
                categoryName = categoryName
            )
            
            val response = googleSheetsRepository.manageCategory(request)
            
            if (response.success) {
                // Crear categoría en base de datos local
                db.withTransaction {
                    val category = Category(
                        externalId = categoryId,
                        name = categoryName,
                        type = entryType,
                        createdOn = LocalDateTime.now()
                    )
                    categoryRepository.saveCategory(category)
                }
            }
            
            response
        } catch (e: Exception) {
            CategoryManagementResponse(
                success = false,
                message = "Error al crear categoría: ${e.message}"
            )
        }
    }

    override suspend fun createSubcategory(
        categoryId: String,
        subcategoryName: String,
        subcategoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse {
        return try {
            // Crear subcategoría en Google Sheets
            val request = CategoryManagementRequest(
                action = "createSubcategory",
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                categoryId = categoryId,
                subcategoryId = subcategoryId,
                subcategoryName = subcategoryName
            )
            
            val response = googleSheetsRepository.manageCategory(request)
            
            if (response.success) {
                // Crear subcategoría en base de datos local
                db.withTransaction {
                    val category = categoryRepository.findCategoryByExternalIdAndEntryType(categoryId, entryType)
                    val subcategory = Subcategory(
                        categoryId = category.uid,
                        externalId = subcategoryId,
                        name = subcategoryName,
                        createdOn = LocalDateTime.now()
                    )
                    val subcategoryUid = subcategoryRepository.saveSubcategory(subcategory)
                    
                    // Crear referencia de fila
                    val subcategoryRow = SubcategoryRow(
                        subcategoryId = subcategoryUid,
                        rowNumber = response.rowNumber ?: 0
                    )
                    subcategoryRowRepository.saveSubcategoryRow(subcategoryRow)
                }
            }
            
            response
        } catch (e: Exception) {
            CategoryManagementResponse(
                success = false,
                message = "Error al crear subcategoría: ${e.message}"
            )
        }
    }

    override suspend fun updateCategory(
        categoryId: String,
        newName: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse {
        return try {
            // Actualizar categoría en Google Sheets
            val request = CategoryManagementRequest(
                action = "updateCategory",
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                categoryId = categoryId,
                newName = newName
            )
            
            val response = googleSheetsRepository.manageCategory(request)
            
            if (response.success) {
                // Actualizar categoría en base de datos local
                db.withTransaction {
                    val category = categoryRepository.findCategoryByExternalIdAndEntryType(categoryId, entryType)
                    categoryRepository.updateCategoryName(category.uid, newName)
                }
            }
            
            response
        } catch (e: Exception) {
            CategoryManagementResponse(
                success = false,
                message = "Error al actualizar categoría: ${e.message}"
            )
        }
    }

    override suspend fun updateSubcategory(
        subcategoryId: String,
        newName: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse {
        return try {
            // Actualizar subcategoría en Google Sheets
            val request = CategoryManagementRequest(
                action = "updateSubcategory",
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                subcategoryId = subcategoryId,
                newName = newName
            )
            
            val response = googleSheetsRepository.manageCategory(request)
            
            if (response.success) {
                // Actualizar subcategoría en base de datos local
                db.withTransaction {
                    val subcategory = subcategoryRepository.findSubcategoryByExternalIdAndCategoryType(entryType, subcategoryId)
                    subcategoryRepository.updateSubcategoryName(subcategory.uid, newName)
                }
            }
            
            response
        } catch (e: Exception) {
            CategoryManagementResponse(
                success = false,
                message = "Error al actualizar subcategoría: ${e.message}"
            )
        }
    }

    override suspend fun deleteCategory(
        categoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse {
        return try {
            // Eliminar categoría en Google Sheets
            val request = CategoryManagementRequest(
                action = "deleteCategory",
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                categoryId = categoryId
            )
            
            val response = googleSheetsRepository.manageCategory(request)
            
            if (response.success) {
                // Eliminar categoría y subcategorías en base de datos local
                db.withTransaction {
                    val category = categoryRepository.findCategoryByExternalIdAndEntryType(categoryId, entryType)
                    val subcategories = subcategoryRepository.findAllSubcategoriesByCategoryId(category.uid)
                    
                    // Eliminar subcategorías y sus filas
                    subcategories.forEach { subcategoryAndRow ->
                        subcategoryRowRepository.deleteSubcategoryRow(subcategoryAndRow.subcategoryRow)
                        subcategoryRepository.deleteSubcategory(subcategoryAndRow.subcategory)
                    }
                    
                    // Eliminar categoría
                    categoryRepository.deleteCategory(category)
                }
            }
            
            response
        } catch (e: Exception) {
            CategoryManagementResponse(
                success = false,
                message = "Error al eliminar categoría: ${e.message}"
            )
        }
    }

    override suspend fun deleteSubcategory(
        subcategoryId: String,
        entryType: EntryType,
        spreadsheetId: String,
        sheetName: String
    ): CategoryManagementResponse {
        return try {
            // Eliminar subcategoría en Google Sheets
            val request = CategoryManagementRequest(
                action = "deleteSubcategory",
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                subcategoryId = subcategoryId
            )
            
            val response = googleSheetsRepository.manageCategory(request)
            
            if (response.success) {
                // Eliminar subcategoría en base de datos local
                db.withTransaction {
                    val subcategory = subcategoryRepository.findSubcategoryByExternalIdAndCategoryType(entryType, subcategoryId)
                    val subcategoryRow = subcategoryRowRepository.findRowBySubcategoryId(subcategory.uid)
                    
                    subcategoryRowRepository.deleteSubcategoryRow(subcategoryRow)
                    subcategoryRepository.deleteSubcategory(subcategory)
                }
            }
            
            response
        } catch (e: Exception) {
            CategoryManagementResponse(
                success = false,
                message = "Error al eliminar subcategoría: ${e.message}"
            )
        }
    }
}

