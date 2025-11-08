package com.eflc.mintdrop.di

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.eflc.mintdrop.worker.PeriodicSyncWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        val workManager = WorkManager.getInstance(context)

        // Configurar trabajo periódico para verificar tareas pendientes
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncWork = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(
            30, TimeUnit.MINUTES,
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("periodic_sync")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodic_sync_work",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncWork
        )

        return workManager
    }
}

