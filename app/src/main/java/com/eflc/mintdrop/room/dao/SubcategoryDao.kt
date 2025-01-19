package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndEntryHistory
import com.eflc.mintdrop.room.dao.entity.relationship.SubcategoryAndSubcategoryRow

@Dao
interface SubcategoryDao {
    @Upsert
    suspend fun saveSubcategory(subcategory: Subcategory): Long

    @Query("SELECT * FROM subcategory WHERE uid = :subcategoryId")
    suspend fun getSubcategory(subcategoryId: Long): Subcategory

    @Transaction
    @Query("SELECT * FROM subcategory WHERE category_id = :categoryId ORDER BY uid ASC")
    fun getSubcategoriesByCategoryId(categoryId: Long): List<SubcategoryAndSubcategoryRow>

    @Transaction
    @Query("SELECT * FROM subcategory WHERE uid = :subcategoryId")
    fun getSubcategoryWithEntryHistory(subcategoryId: Long): SubcategoryAndEntryHistory

    @Query("""
        SELECT sc.* FROM subcategory sc
        INNER JOIN category c ON sc.category_id = c.uid
        WHERE c.type = :categoryType AND sc.external_id = :externalId
    """)
    fun getSubcategoryByExternalIdAndCategoryType(categoryType: EntryType, externalId: String): Subcategory

    @Query("""
        SELECT sub.* FROM subcategory sub
        JOIN category cat ON cat.uid = sub.category_id
        WHERE cat.type = "EXPENSE"
        ORDER BY sub.last_entry_on DESC LIMIT :limit
    """)
    @Transaction
    fun getLastXSubcategoriesOrderedByLastEntry(limit: Int): List<SubcategoryAndSubcategoryRow>
}
