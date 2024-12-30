package com.eflc.mintdrop.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eflc.mintdrop.room.dao.CategoryDao
import com.eflc.mintdrop.room.dao.EntryHistoryDao
import com.eflc.mintdrop.room.dao.ExternalSheetRefDao
import com.eflc.mintdrop.room.dao.PaymentMethodDao
import com.eflc.mintdrop.room.dao.SharedExpenseConfigurationDao
import com.eflc.mintdrop.room.dao.SharedExpenseConfigurationDetailDao
import com.eflc.mintdrop.room.dao.SharedExpenseEntryDetailDao
import com.eflc.mintdrop.room.dao.SharedExpenseSettlementDao
import com.eflc.mintdrop.room.dao.SubcategoryDao
import com.eflc.mintdrop.room.dao.SubcategoryMonthlyBalanceDao
import com.eflc.mintdrop.room.dao.SubcategoryRowDao
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.ExternalSheetRef
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.SharedExpenseConfiguration
import com.eflc.mintdrop.room.dao.entity.SharedExpenseConfigurationDetail
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.room.dao.entity.SharedExpenseSettlement
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance
import com.eflc.mintdrop.room.dao.entity.SubcategoryRow

@Database(
    entities = [
        Category::class,
        Subcategory::class,
        SubcategoryRow::class,
        EntryHistory::class,
        PaymentMethod::class,
        SubcategoryMonthlyBalance::class,
        SharedExpenseConfiguration::class,
        SharedExpenseEntryDetail::class,
        SharedExpenseConfigurationDetail::class,
        SharedExpenseSettlement::class,
        ExternalSheetRef::class
    ],
    version = 5
)
@TypeConverters(Converters::class)
abstract class JulepDatabase: RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val subcategoryDao: SubcategoryDao
    abstract val subcategoryRowDao: SubcategoryRowDao
    abstract val entryHistoryDao: EntryHistoryDao
    abstract val paymentMethodDao: PaymentMethodDao
    abstract val subcategoryMonthlyBalanceDao: SubcategoryMonthlyBalanceDao
    abstract val externalSheetRefDao: ExternalSheetRefDao
    abstract val sharedExpenseSettlementDao: SharedExpenseSettlementDao
    abstract val sharedExpenseConfigurationDao: SharedExpenseConfigurationDao
    abstract val sharedExpenseConfigurationDetailDao: SharedExpenseConfigurationDetailDao
    abstract val sharedExpenseEntryDetailDao: SharedExpenseEntryDetailDao
}
