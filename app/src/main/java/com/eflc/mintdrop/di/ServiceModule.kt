package com.eflc.mintdrop.di

import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.repository.SharedExpenseRepository
import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.service.record.EntryRecordService
import com.eflc.mintdrop.service.record.impl.EntryRecordServiceImpl
import com.eflc.mintdrop.service.shared.SharedExpenseService
import com.eflc.mintdrop.service.shared.impl.SharedExpenseServiceImpl
import com.eflc.mintdrop.service.sync.OutboxEnqueuer
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
        categoryRepository: CategoryRepository,
        externalSheetRefRepository: ExternalSheetRefRepository,
        sharedExpenseService: SharedExpenseService,
        outboxEnqueuer: OutboxEnqueuer
    ): EntryRecordService {
        return EntryRecordServiceImpl(
            db,
            entryHistoryRepository,
            subcategoryRepository,
            categoryRepository,
            subcategoryRowRepository,
            subcategoryMonthlyBalanceRepository,
            externalSheetRefRepository,
            sharedExpenseService,
            outboxEnqueuer
        )
    }

    @Provides
    @Singleton
    fun provideSharedExpenseService(
        db: JulepDatabase,
        sharedExpenseRepository: SharedExpenseRepository,
        entryHistoryRepository: EntryHistoryRepository
    ): SharedExpenseService {
        return SharedExpenseServiceImpl(
            db,
            sharedExpenseRepository,
            entryHistoryRepository
        )
    }
}
