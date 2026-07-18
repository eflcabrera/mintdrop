package com.eflc.mintdrop.service.sync

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
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
import java.time.LocalDateTime
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

    /**
     * @param replaceExisting REPLACE cancela un worker en vuelo; usar KEEP en recovery.
     */
    fun scheduleSync(taskId: Long, replaceExisting: Boolean = true) {
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

        workManager.enqueueUniqueWork(
            TASK_WORK_NAME_PREFIX + taskId,
            if (replaceExisting) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP,
            request
        )
    }

    /**
     * Recupera tareas atascadas y reencola workers para todo lo procesable.
     * Se invoca al arrancar la app y desde el worker periódico.
     */
    suspend fun recoverAndSchedulePendingTasks(limit: Int = DEFAULT_BATCH_LIMIT) {
        val dao = db.pendingSyncTaskDao
        val staleBefore = LocalDateTime.now().minusMinutes(STALE_IN_PROGRESS_MINUTES)

        val staleTasks = dao.getStaleInProgressTasks(staleBefore)
        if (staleTasks.isNotEmpty()) {
            Log.w(TAG, "Recuperando ${staleTasks.size} tareas IN_PROGRESS huérfanas")
        }
        staleTasks.forEach { task ->
            dao.updateTaskStatus(
                taskId = task.uid,
                status = SyncStatus.PENDING,
                attemptCount = task.attemptCount,
                lastAttemptOn = task.lastAttemptOn,
                errorMessage = "Recuperada de IN_PROGRESS huérfana"
            )
        }

        val pendingTasks = dao.getPendingTasks(limit = limit)
        if (pendingTasks.isNotEmpty()) {
            Log.d(TAG, "Reencolando ${pendingTasks.size} tareas pendientes de sync")
        }
        // KEEP: no matar un SyncWorker que aún está corriendo (Apps Script lento)
        pendingTasks.forEach { task ->
            scheduleSync(task.uid, replaceExisting = false)
        }
    }

    suspend fun purgeCompletedTasksOlderThan(days: Long = 7) {
        val cutoff = LocalDateTime.now().minusDays(days)
        db.pendingSyncTaskDao.deleteCompletedTasksOlderThan(SyncStatus.COMPLETED, cutoff)
    }

    /**
     * Cancela tareas PENDING/FAILED del entry. Si hay alguna IN_PROGRESS, el POST
     * puede haber salido o estar en curso → hace falta UNDO compensatorio.
     *
     * @return true si se debe encolar UNDO compensatorio
     */
    suspend fun cancelActiveTasksForEntry(entryHistoryId: Long): Boolean {
        // Contar COMPLETED *antes* de marcar canceladas (si no, PENDING cancelado cuenta)
        val hadPriorCompletedCreate =
            db.pendingSyncTaskDao.countCompletedTasksForEntry(entryHistoryId) > 0

        val tasks = db.pendingSyncTaskDao.getActiveTasksByEntryHistoryId(entryHistoryId)
        var hadInProgress = false

        tasks.forEach { task ->
            workManager.cancelUniqueWork(TASK_WORK_NAME_PREFIX + task.uid)
            when (task.status) {
                SyncStatus.IN_PROGRESS -> {
                    hadInProgress = true
                    db.pendingSyncTaskDao.markAsCompleted(task.uid)
                    Log.w(TAG, "Create IN_PROGRESS cancelado para entry=$entryHistoryId; requiere UNDO")
                }
                SyncStatus.PENDING, SyncStatus.FAILED -> {
                    db.pendingSyncTaskDao.markAsCompleted(task.uid)
                }
                else -> Unit
            }
        }

        val needsCompensatingUndo = hadInProgress || hadPriorCompletedCreate
        if (needsCompensatingUndo && hadPriorCompletedCreate && !hadInProgress) {
            Log.w(TAG, "Create COMPLETED previo para entry=$entryHistoryId; requiere UNDO")
        }
        return needsCompensatingUndo
    }

    /**
     * Encola UNDO si no hay ya uno activo para el mismo entry.
     * [createOrUndoPayload] puede ser el create original (se niega) o un undo ya armado.
     */
    suspend fun enqueueUndoIfAbsent(
        entryHistoryId: Long,
        spreadsheetId: String,
        sheetName: String,
        month: Int,
        row: Int,
        originalAmount: Double,
        originalDescription: String
    ): Long? {
        val active = db.pendingSyncTaskDao.getActiveTasksByEntryHistoryId(entryHistoryId)
        val hasActiveUndo = active.any { task ->
            runCatching {
                moshi.adapter(SyncPayload::class.java).fromJson(task.payload)?.let { isUndoPayload(it) }
            }.getOrNull() == true
        }
        if (hasActiveUndo) {
            Log.d(TAG, "UNDO ya activo para entry=$entryHistoryId; skip")
            return null
        }

        val description = if (originalDescription.startsWith("UNDO ")) {
            originalDescription
        } else {
            "UNDO $originalDescription"
        }

        return enqueue(
            SyncPayload(
                operationId = newOperationId(),
                entryHistoryId = entryHistoryId,
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                month = month,
                row = row,
                amount = -kotlin.math.abs(originalAmount),
                description = description,
                isOwedInstallments = false,
                totalInstallments = 1,
                paymentMethod = ""
            )
        )
    }

    /**
     * Encola UNDO compensatorio a partir de un payload de create (p. ej. desde SyncWorker).
     */
    suspend fun enqueueCompensatingUndoIfNeeded(createPayload: SyncPayload): Long? {
        if (isUndoPayload(createPayload)) {
            return null
        }
        return enqueueUndoIfAbsent(
            entryHistoryId = createPayload.entryHistoryId,
            spreadsheetId = createPayload.spreadsheetId,
            sheetName = createPayload.sheetName,
            month = createPayload.month,
            row = createPayload.row,
            originalAmount = createPayload.amount,
            originalDescription = createPayload.description
        )
    }

    suspend fun retryFailedTask(entryHistoryId: Long): Long? {
        val task = db.pendingSyncTaskDao.getFailedTaskByEntryHistoryId(entryHistoryId) ?: return null
        db.pendingSyncTaskDao.resetTaskForRetry(task.uid)
        scheduleSync(task.uid, replaceExisting = true)
        return task.uid
    }

    fun observeFailedEntryHistoryIds(): Flow<Set<Long>> {
        return db.pendingSyncTaskDao.observeFailedEntryHistoryIds()
            .map { ids -> ids.filterNotNull().toSet() }
    }

    fun newOperationId(): String = UUID.randomUUID().toString()

    private fun isUndoPayload(payload: SyncPayload): Boolean {
        return payload.description.startsWith("UNDO ") || payload.amount < 0
    }

    companion object {
        private const val TAG = "OutboxEnqueuer"
        const val SYNC_EXPENSE_TAG = "sync_expense_entry"
        const val TASK_TAG_PREFIX = "sync_task_"
        const val TASK_WORK_NAME_PREFIX = "sync_task_work_"
        /** Apps Script puede tardar; no recuperar IN_PROGRESS demasiado pronto. */
        const val STALE_IN_PROGRESS_MINUTES = 30L
        const val DEFAULT_BATCH_LIMIT = 50

        fun networkConstraints(): Constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
    }
}
