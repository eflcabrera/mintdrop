package com.eflc.mintdrop.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eflc.mintdrop.service.sync.OutboxEnqueuer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PeriodicSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val outboxEnqueuer: OutboxEnqueuer
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        outboxEnqueuer.recoverAndSchedulePendingTasks()
        outboxEnqueuer.purgeCompletedTasksOlderThan()
        return Result.success()
    }
}
