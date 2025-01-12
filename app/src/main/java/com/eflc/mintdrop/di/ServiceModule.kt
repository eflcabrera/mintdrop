package com.eflc.mintdrop.di

import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.service.record.EntryRecordService
import com.eflc.mintdrop.service.record.impl.EntryRecordServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideEntryRecordService(
        db: JulepDatabase,
        entryHistoryRepository: EntryHistoryRepository,
        subcategoryRepository: SubcategoryRepository,
        subcategoryRowRepository: SubcategoryRowRepository,
        subcategoryMonthlyBalanceRepository: SubcategoryMonthlyBalanceRepository,
        googleSheetsRepository: GoogleSheetsRepository,
        categoryRepository: CategoryRepository,
        externalSheetRefRepository: ExternalSheetRefRepository
    ): EntryRecordService {
        return EntryRecordServiceImpl(
            db,
            entryHistoryRepository,
            subcategoryRepository,
            categoryRepository,
            subcategoryRowRepository,
            subcategoryMonthlyBalanceRepository,
            googleSheetsRepository,
            externalSheetRefRepository
        )
    }
}
