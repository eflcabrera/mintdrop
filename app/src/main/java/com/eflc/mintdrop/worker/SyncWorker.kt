package com.eflc.mintdrop.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.eflc.mintdrop.models.SyncPayload
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.SyncStatus
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
    private val moshi: Moshi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(TASK_ID_KEY, -1L)
        if (taskId == -1L) {
            return Result.failure()
        }

        val pendingSyncTaskDao = db.pendingSyncTaskDao
        val entryHistoryDao = db.entryHistoryDao

        // Obtener la tarea
        val task = pendingSyncTaskDao.getTaskById(taskId) ?: return Result.failure()

        // Actualizar estado a IN_PROGRESS
        val attemptCount = task.attemptCount + 1
        pendingSyncTaskDao.updateTaskStatus(
            taskId = task.uid,
            status = SyncStatus.IN_PROGRESS,
            attemptCount = attemptCount,
            lastAttemptOn = LocalDateTime.now(),
            errorMessage = null
        )

        return try {
            // Deserializar payload
            val payloadJsonAdapter = moshi.adapter(SyncPayload::class.java)
            val payload = payloadJsonAdapter.fromJson(task.payload)
                ?: return handleFailure(task.uid, attemptCount, "Error deserializando payload")

            // Validar que sea EXPENSE_ENTRY
            if (task.taskType.name != "EXPENSE_ENTRY") {
                return handleFailure(task.uid, attemptCount, "Tipo de tarea no soportado: ${task.taskType}")
            }

            // Llamar a la API
            val request = payload.toExpenseEntryRequest()
            val response = googleSheetsRepository.postExpense(request)

            // Si llegamos aquí sin excepción, la operación fue exitosa
            // Marcar tarea como completada
            pendingSyncTaskDao.markAsCompleted(task.uid, SyncStatus.COMPLETED, LocalDateTime.now())
            
            // Marcar EntryHistory como sincronizada
            entryHistoryDao.markAsSyncedToSheets(payload.entryHistoryId)
            
            Result.success()
        } catch (e: Exception) {
            handleFailure(task.uid, attemptCount, e.message ?: "Error desconocido")
        }
    }

    private suspend fun handleFailure(taskId: Long, attemptCount: Int, errorMessage: String): Result {
        val pendingSyncTaskDao = db.pendingSyncTaskDao
        
        // Obtener la tarea actualizada para verificar maxAttempts
        val task = pendingSyncTaskDao.getTaskById(taskId) ?: return Result.failure()
        
        val finalStatus = if (attemptCount >= task.maxAttempts) {
            SyncStatus.FAILED
        } else {
            SyncStatus.PENDING  // Para reintentar
        }

        pendingSyncTaskDao.updateTaskStatus(
            taskId = taskId,
            status = finalStatus,
            attemptCount = attemptCount,
            lastAttemptOn = LocalDateTime.now(),
            errorMessage = errorMessage
        )

        // Si alcanzó el máximo de intentos, retornar failure
        // Si aún tiene intentos, retornar retry para que WorkManager reintente
        return if (attemptCount >= task.maxAttempts) {
            Result.failure(workDataOf(ERROR_KEY to errorMessage))
        } else {
            Result.retry()
        }
    }

    companion object {
        const val TASK_ID_KEY = "task_id"
        const val ERROR_KEY = "error_message"

        fun createInputData(taskId: Long) = workDataOf(TASK_ID_KEY to taskId)
    }
}

