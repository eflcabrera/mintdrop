package com.eflc.mintdrop.di

import android.app.Application
import androidx.room.Room
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.repository.impl.CategoryRepositoryImpl
import com.eflc.mintdrop.repository.impl.EntryHistoryRepositoryImpl
import com.eflc.mintdrop.repository.impl.SubcategoryRepositoryImpl
import com.eflc.mintdrop.repository.impl.SubcategoryRowRepositoryImpl
import com.eflc.mintdrop.room.JulepDatabase
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
        .addMigrations()
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
}
