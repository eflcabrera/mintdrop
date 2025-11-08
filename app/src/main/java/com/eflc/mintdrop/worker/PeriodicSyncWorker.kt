package com.eflc.mintdrop.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.SyncStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PeriodicSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val db: JulepDatabase,
    private val workManager: WorkManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pendingSyncTaskDao = db.pendingSyncTaskDao

        // Obtener tareas pendientes o fallidas con intentos disponibles
        val pendingTasks = pendingSyncTaskDao.getPendingTasks(limit = 10)

        // Encolar un SyncWorker para cada tarea pendiente
        pendingTasks.forEach { task ->
            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInputData(SyncWorker.createInputData(task.uid))
                .build()

            workManager.enqueue(syncWorkRequest)
        }

        // Limpiar tareas completadas antiguas (más de 7 días)
        val sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7)
        pendingSyncTaskDao.deleteCompletedTasksOlderThan(
            SyncStatus.COMPLETED,
            sevenDaysAgo
        )

        return Result.success()
    }
}

