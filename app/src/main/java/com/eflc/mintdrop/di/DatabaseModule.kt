package com.eflc.mintdrop.di

import android.app.Application
import androidx.room.Room
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.repository.PaymentMethodRepository
import com.eflc.mintdrop.repository.SharedExpenseRepository
import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.repository.impl.CategoryRepositoryImpl
import com.eflc.mintdrop.repository.impl.EntryHistoryRepositoryImpl
import com.eflc.mintdrop.repository.impl.ExternalSheetRefRepositoryImpl
import com.eflc.mintdrop.repository.impl.PaymentMethodRepositoryImpl
import com.eflc.mintdrop.repository.impl.SharedExpenseRepositoryImpl
import com.eflc.mintdrop.repository.impl.SubcategoryMonthlyBalanceRepositoryImpl
import com.eflc.mintdrop.repository.impl.SubcategoryRepositoryImpl
import com.eflc.mintdrop.repository.impl.SubcategoryRowRepositoryImpl
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.migration.MigrationFrom2To3
import com.eflc.mintdrop.room.migration.MigrationFrom3To4
import com.eflc.mintdrop.room.migration.MigrationFrom4To5
import com.eflc.mintdrop.room.migration.MigrationFrom5To6
import com.eflc.mintdrop.room.migration.MigrationFrom6To7
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): JulepDatabase {
        return Room.databaseBuilder(
            app,
            JulepDatabase::class.java,
            "julep.db")
        .addMigrations(
            MigrationFrom2To3(),
            MigrationFrom3To4(),
            MigrationFrom4To5(),
            MigrationFrom5To6(),
            MigrationFrom6To7()
        )
        .build()
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(db: JulepDatabase): CategoryRepository {
        return CategoryRepositoryImpl(db.categoryDao)
    }

    @Provides
    @Singleton
    fun provideSubcategoryRepository(db: JulepDatabase): SubcategoryRepository {
        return SubcategoryRepositoryImpl(db.subcategoryDao)
    }

    @Provides
    @Singleton
    fun provideSubcategoryRowRepository(db: JulepDatabase): SubcategoryRowRepository {
        return SubcategoryRowRepositoryImpl(db.subcategoryRowDao)
    }

    @Provides
    @Singleton
    fun provideEntryHistoryRepository(db: JulepDatabase): EntryHistoryRepository {
        return EntryHistoryRepositoryImpl(db.entryHistoryDao)
    }

    @Provides
    @Singleton
    fun providePaymentMethodRepository(db: JulepDatabase): PaymentMethodRepository {
        return PaymentMethodRepositoryImpl(db.paymentMethodDao)
    }

    @Provides
    @Singleton
    fun provideSubcategoryMonthlyBalanceRepository(db: JulepDatabase): SubcategoryMonthlyBalanceRepository {
        return SubcategoryMonthlyBalanceRepositoryImpl(db.subcategoryMonthlyBalanceDao)
    }

    @Provides
    @Singleton
    fun provideSharedExpenseRepository(db: JulepDatabase): SharedExpenseRepository {
        return SharedExpenseRepositoryImpl(
            db.sharedExpenseConfigurationDao,
            db.sharedExpenseConfigurationDetailDao,
            db.sharedExpenseEntryDetailDao,
            db.sharedExpenseSettlementDao
        )
    }

    @Provides
    @Singleton
    fun provideExternalSheetRefRepository(db: JulepDatabase): ExternalSheetRefRepository {
        return ExternalSheetRefRepositoryImpl(db.externalSheetRefDao)
    }
}
