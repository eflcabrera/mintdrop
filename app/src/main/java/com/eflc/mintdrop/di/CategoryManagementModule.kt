package com.eflc.mintdrop.di

import com.eflc.mintdrop.service.category.CategoryManagementService
import com.eflc.mintdrop.service.category.impl.CategoryManagementServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoryManagementModule {

    @Binds
    abstract fun bindCategoryManagementService(
        categoryManagementServiceImpl: CategoryManagementServiceImpl
    ): CategoryManagementService
}

