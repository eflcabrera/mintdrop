package com.eflc.mintdrop.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.eflc.mintdrop.models.SyncPayload
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.SyncStatus
import com.eflc.mintdrop.room.dao.entity.SyncTaskType
import com.eflc.mintdrop.service.sync.OutboxEnqueuer
import com.squareup.moshi.Moshi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val db: JulepDatabase,
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val moshi: Moshi,
    private val outboxEnqueuer: OutboxEnqueuer
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(TASK_ID_KEY, -1L)
        if (taskId == -1L) {
            return Result.failure()
        }

        val pendingSyncTaskDao = db.pendingSyncTaskDao
        val entryHistoryDao = db.entryHistoryDao

        val task = pendingSyncTaskDao.getTaskById(taskId) ?: return Result.failure()

        // Tarea ya finalizada (p.ej. cancelada por undo local)
        if (task.status == SyncStatus.COMPLETED) {
            return Result.success()
        }

        val attemptCount = task.attemptCount + 1
        pendingSyncTaskDao.updateTaskStatus(
            taskId = task.uid,
            status = SyncStatus.IN_PROGRESS,
            attemptCount = attemptCount,
            lastAttemptOn = LocalDateTime.now(),
            errorMessage = null
        )

        return try {
            val payload = moshi.adapter(SyncPayload::class.java).fromJson(task.payload)
                ?: return handleFailure(task.uid, attemptCount, "Error deserializando payload")

            if (task.taskType != SyncTaskType.EXPENSE_ENTRY) {
                return handleFailure(task.uid, attemptCount, "Tipo de tarea no soportado: ${task.taskType}")
            }

            // deleteRecord puede haber marcado COMPLETED mientras pasábamos a IN_PROGRESS
            val latest = pendingSyncTaskDao.getTaskById(taskId)
            if (latest?.status == SyncStatus.COMPLETED) {
                Log.d(TAG, "Tarea $taskId cancelada antes del POST; abortando")
                return Result.success()
            }

            val request = payload.toExpenseEntryRequest()
            googleSheetsRepository.postExpense(request)

            pendingSyncTaskDao.markAsCompleted(task.uid, SyncStatus.COMPLETED, LocalDateTime.now())

            val entryStillExists = entryHistoryDao.findEntryHistoryOrNull(payload.entryHistoryId) != null
            if (entryStillExists) {
                entryHistoryDao.markAsSyncedToSheets(payload.entryHistoryId)
            } else {
                // Create llegó al Sheet pero la entrada local ya se borró → UNDO compensatorio
                Log.w(TAG, "Entry ${payload.entryHistoryId} borrada durante sync; encolando UNDO compensatorio")
                val undoTaskId = outboxEnqueuer.enqueueCompensatingUndoIfNeeded(payload)
                undoTaskId?.let { outboxEnqueuer.scheduleSync(it) }
            }

            Result.success()
        } catch (e: Exception) {
            // Si nos cancelaron a COMPLETED mientras corría, no reintentar
            val current = pendingSyncTaskDao.getTaskById(taskId)
            if (current?.status == SyncStatus.COMPLETED) {
                return Result.success()
            }
            handleFailure(task.uid, attemptCount, e.message ?: "Error desconocido")
        }
    }

    private suspend fun handleFailure(taskId: Long, attemptCount: Int, errorMessage: String): Result {
        val pendingSyncTaskDao = db.pendingSyncTaskDao
        val task = pendingSyncTaskDao.getTaskById(taskId) ?: return Result.failure()

        if (task.status == SyncStatus.COMPLETED) {
            return Result.success()
        }

        val finalStatus = if (attemptCount >= task.maxAttempts) {
            SyncStatus.FAILED
        } else {
            SyncStatus.PENDING
        }

        pendingSyncTaskDao.updateTaskStatus(
            taskId = taskId,
            status = finalStatus,
            attemptCount = attemptCount,
            lastAttemptOn = LocalDateTime.now(),
            errorMessage = errorMessage
        )

        return if (attemptCount >= task.maxAttempts) {
            Result.failure(workDataOf(ERROR_KEY to errorMessage))
        } else {
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        const val TASK_ID_KEY = "task_id"
        const val ERROR_KEY = "error_message"

        fun createInputData(taskId: Long) = workDataOf(TASK_ID_KEY to taskId)
    }
}
