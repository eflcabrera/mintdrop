package com.eflc.mintdrop.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eflc.mintdrop.repository.dao.CategoryDao
import com.eflc.mintdrop.repository.dao.EntryHistoryDao
import com.eflc.mintdrop.repository.dao.SubcategoryDao
import com.eflc.mintdrop.repository.dao.SubcategoryRowDao
import com.eflc.mintdrop.repository.dao.entity.Category
import com.eflc.mintdrop.repository.dao.entity.EntryHistory
import com.eflc.mintdrop.repository.dao.entity.Subcategory
import com.eflc.mintdrop.repository.dao.entity.SubcategoryRow

@Database(
    entities = [
        Category::class,
        Subcategory::class,
        SubcategoryRow::class,
        EntryHistory::class
    ],
    version = 1
)
abstract class JulepDatabase: RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val subcategory: SubcategoryDao
    abstract val subcategoryRowDao: SubcategoryRowDao
    abstract val entryHistoryDao: EntryHistoryDao
}