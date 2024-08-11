package com.eflc.mintdrop.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eflc.mintdrop.room.dao.CategoryDao
import com.eflc.mintdrop.room.dao.EntryHistoryDao
import com.eflc.mintdrop.room.dao.SubcategoryDao
import com.eflc.mintdrop.room.dao.SubcategoryRowDao
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.SubcategoryRow

@Database(
    entities = [
        Category::class,
        Subcategory::class,
        SubcategoryRow::class,
        EntryHistory::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class JulepDatabase: RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val subcategoryDao: SubcategoryDao
    abstract val subcategoryRowDao: SubcategoryRowDao
    abstract val entryHistoryDao: EntryHistoryDao
}