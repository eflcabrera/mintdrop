package com.eflc.mintdrop.service.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.eflc.mintdrop.models.SyncPayload
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.PendingSyncTask
import com.eflc.mintdrop.room.dao.entity.SyncStatus
import com.eflc.mintdrop.room.dao.entity.SyncTaskType
import com.eflc.mintdrop.worker.SyncWorker
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inserta tareas en el outbox (debe llamarse dentro de withTransaction)
 * y programa el procesamiento con WorkManager fuera de la transacción.
 */
@Singleton
class OutboxEnqueuer @Inject constructor(
    private val db: JulepDatabase,
    private val moshi: Moshi,
    private val workManager: WorkManager
) {

    /**
     * Persiste la tarea en Room. Debe ejecutarse dentro de la misma
     * [androidx.room.withTransaction] que la escritura de dominio.
     */
    suspend fun enqueue(
        payload: SyncPayload,
        taskType: SyncTaskType = SyncTaskType.EXPENSE_ENTRY
    ): Long {
        val payloadJson = moshi.adapter(SyncPayload::class.java).toJson(payload)
        val task = PendingSyncTask(
            operationId = payload.operationId,
            entryHistoryId = payload.entryHistoryId,
            taskType = taskType,
            payload = payloadJson,
            status = SyncStatus.PENDING
        )
        return db.pendingSyncTaskDao.insertOrUpdateTask(task)
    }

    fun scheduleSync(taskId: Long) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(SyncWorker.createInputData(taskId))
            .setConstraints(networkConstraints())
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag(TASK_TAG_PREFIX + taskId)
            .addTag(SYNC_EXPENSE_TAG)
            .build()
        workManager.enqueue(request)
    }

    suspend fun cancelActiveTasksForEntry(entryHistoryId: Long) {
        val tasks = db.pendingSyncTaskDao.getActiveTasksByEntryHistoryId(entryHistoryId)
        tasks.forEach { task ->
            workManager.cancelAllWorkByTag(TASK_TAG_PREFIX + task.uid)
            db.pendingSyncTaskDao.markAsCompleted(task.uid)
        }
    }

    suspend fun retryFailedTask(entryHistoryId: Long): Long? {
        val task = db.pendingSyncTaskDao.getFailedTaskByEntryHistoryId(entryHistoryId) ?: return null
        db.pendingSyncTaskDao.resetTaskForRetry(task.uid)
        scheduleSync(task.uid)
        return task.uid
    }

    fun observeFailedEntryHistoryIds(): Flow<Set<Long>> {
        return db.pendingSyncTaskDao.observeFailedEntryHistoryIds()
            .map { ids -> ids.filterNotNull().toSet() }
    }

    fun newOperationId(): String = UUID.randomUUID().toString()

    companion object {
        const val SYNC_EXPENSE_TAG = "sync_expense_entry"
        const val TASK_TAG_PREFIX = "sync_task_"

        fun networkConstraints(): Constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
    }
}
