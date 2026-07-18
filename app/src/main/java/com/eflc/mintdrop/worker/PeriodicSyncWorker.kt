package com.eflc.mintdrop.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.SyncStatus
import com.eflc.mintdrop.service.sync.OutboxEnqueuer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class PeriodicSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val db: JulepDatabase,
    private val workManager: WorkManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pendingSyncTaskDao = db.pendingSyncTaskDao

        // Recuperar tareas IN_PROGRESS huérfanas (proceso muerto mid-sync)
        val staleBefore = LocalDateTime.now().minusMinutes(STALE_IN_PROGRESS_MINUTES)
        pendingSyncTaskDao.getStaleInProgressTasks(staleBefore).forEach { task ->
            pendingSyncTaskDao.updateTaskStatus(
                taskId = task.uid,
                status = SyncStatus.PENDING,
                attemptCount = task.attemptCount,
                lastAttemptOn = task.lastAttemptOn,
                errorMessage = "Recuperada de IN_PROGRESS huérfana"
            )
        }

        val pendingTasks = pendingSyncTaskDao.getPendingTasks(limit = 10)

        pendingTasks.forEach { task ->
            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInputData(SyncWorker.createInputData(task.uid))
                .setConstraints(OutboxEnqueuer.networkConstraints())
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag(OutboxEnqueuer.TASK_TAG_PREFIX + task.uid)
                .addTag(OutboxEnqueuer.SYNC_EXPENSE_TAG)
                .build()

            workManager.enqueue(syncWorkRequest)
        }

        val sevenDaysAgo = LocalDateTime.now().minusDays(7)
        pendingSyncTaskDao.deleteCompletedTasksOlderThan(
            SyncStatus.COMPLETED,
            sevenDaysAgo
        )

        return Result.success()
    }

    companion object {
        private const val STALE_IN_PROGRESS_MINUTES = 30L
    }
}
