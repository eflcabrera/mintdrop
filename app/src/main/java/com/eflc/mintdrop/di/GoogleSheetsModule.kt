package com.eflc.mintdrop.di

import com.eflc.mintdrop.api.GoogleSheetsAPI
import com.eflc.mintdrop.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleSheetsModule {
    @Provides
    @Singleton
    fun provideApi(builder: Retrofit.Builder): GoogleSheetsAPI{
        return builder
            .build()
            .create(GoogleSheetsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
    }
}
