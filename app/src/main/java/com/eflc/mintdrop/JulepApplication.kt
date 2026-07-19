package com.eflc.mintdrop

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eflc.mintdrop.service.sync.OutboxEnqueuer
import com.eflc.mintdrop.worker.PendingSyncSchedulerWorker
import com.eflc.mintdrop.worker.PeriodicSyncWorker
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class JulepApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        scheduleSyncWorkers()
    }

    /**
     * Registra el barrido periódico y un one-shot al arrancar para reencolar
     * tareas PENDING que quedaron huérfanas (p. ej. tras un crash o un worker fallido).
     */
    private fun scheduleSyncWorkers() {
        val workManager = WorkManager.getInstance(this)
        val networkConstraints = OutboxEnqueuer.networkConstraints()

        val periodicSyncWork = PeriodicWorkRequestBuilder<PeriodicSyncWorker>(
            30, TimeUnit.MINUTES,
            15, TimeUnit.MINUTES
        )
            .setConstraints(networkConstraints)
            .addTag("periodic_sync")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodic_sync_work",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncWork
        )

        val bootstrapWork = OneTimeWorkRequestBuilder<PendingSyncSchedulerWorker>()
            .addTag("pending_sync_bootstrap")
            .build()

        workManager.enqueueUniqueWork(
            "pending_sync_bootstrap",
            ExistingWorkPolicy.REPLACE,
            bootstrapWork
        )
    }

    override val workManagerConfiguration: Configuration
        get() {
            val workerFactory = EntryPointAccessors
                .fromApplication(this, HiltWorkerFactoryEntryPoint::class.java)
                .hiltWorkerFactory()
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun hiltWorkerFactory(): HiltWorkerFactory
    }
}
