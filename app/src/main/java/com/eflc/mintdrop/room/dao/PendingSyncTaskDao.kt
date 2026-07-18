package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.PendingSyncTask
import com.eflc.mintdrop.room.dao.entity.SyncStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface PendingSyncTaskDao {
    @Upsert
    suspend fun insertOrUpdateTask(task: PendingSyncTask): Long

    @Query("SELECT * FROM pending_sync_task WHERE status = :status ORDER BY created_on ASC LIMIT :limit")
    suspend fun getTasksByStatus(status: SyncStatus, limit: Int = 10): List<PendingSyncTask>

    @Query(
        """
        SELECT * FROM pending_sync_task 
        WHERE (status = :pendingStatus OR (status = :failedStatus AND attempt_count < max_attempts))
        ORDER BY created_on ASC 
        LIMIT :limit
        """
    )
    suspend fun getPendingTasks(
        pendingStatus: SyncStatus = SyncStatus.PENDING,
        failedStatus: SyncStatus = SyncStatus.FAILED,
        limit: Int = 10
    ): List<PendingSyncTask>

    @Query("SELECT * FROM pending_sync_task WHERE uid = :taskId")
    suspend fun getTaskById(taskId: Long): PendingSyncTask?

    @Query(
        """
        SELECT * FROM pending_sync_task
        WHERE entry_history_id = :entryHistoryId
          AND status IN (:pending, :inProgress, :failed)
        """
    )
    suspend fun getActiveTasksByEntryHistoryId(
        entryHistoryId: Long,
        pending: SyncStatus = SyncStatus.PENDING,
        inProgress: SyncStatus = SyncStatus.IN_PROGRESS,
        failed: SyncStatus = SyncStatus.FAILED
    ): List<PendingSyncTask>

    @Query(
        """
        SELECT * FROM pending_sync_task
        WHERE entry_history_id = :entryHistoryId
        ORDER BY created_on ASC
        """
    )
    suspend fun getTasksByEntryHistoryId(entryHistoryId: Long): List<PendingSyncTask>

    @Query(
        """
        SELECT * FROM pending_sync_task
        WHERE entry_history_id = :entryHistoryId AND status = :failedStatus
        ORDER BY created_on DESC
        LIMIT 1
        """
    )
    suspend fun getFailedTaskByEntryHistoryId(
        entryHistoryId: Long,
        failedStatus: SyncStatus = SyncStatus.FAILED
    ): PendingSyncTask?

    @Query(
        """
        SELECT entry_history_id FROM pending_sync_task
        WHERE status = :failedStatus AND entry_history_id IS NOT NULL
        """
    )
    fun observeFailedEntryHistoryIds(
        failedStatus: SyncStatus = SyncStatus.FAILED
    ): Flow<List<Long?>>

    @Query(
        """
        SELECT * FROM pending_sync_task
        WHERE status = :inProgressStatus
          AND (last_attempt_on IS NULL OR last_attempt_on < :staleBefore)
        """
    )
    suspend fun getStaleInProgressTasks(
        staleBefore: LocalDateTime,
        inProgressStatus: SyncStatus = SyncStatus.IN_PROGRESS
    ): List<PendingSyncTask>

    @Query(
        """
        SELECT COUNT(*) FROM pending_sync_task
        WHERE entry_history_id = :entryHistoryId
          AND status = :completedStatus
        """
    )
    suspend fun countCompletedTasksForEntry(
        entryHistoryId: Long,
        completedStatus: SyncStatus = SyncStatus.COMPLETED
    ): Int

    @Update
    suspend fun updateTask(task: PendingSyncTask)

    @Query(
        """
        UPDATE pending_sync_task 
        SET status = :status, 
            attempt_count = :attemptCount, 
            last_attempt_on = :lastAttemptOn,
            error_message = :errorMessage
        WHERE uid = :taskId
        """
    )
    suspend fun updateTaskStatus(
        taskId: Long,
        status: SyncStatus,
        attemptCount: Int,
        lastAttemptOn: LocalDateTime?,
        errorMessage: String?
    )

    @Query(
        """
        UPDATE pending_sync_task 
        SET status = :status, 
            attempt_count = 0,
            error_message = NULL,
            last_attempt_on = NULL
        WHERE uid = :taskId
        """
    )
    suspend fun resetTaskForRetry(taskId: Long, status: SyncStatus = SyncStatus.PENDING)

    @Query(
        """
        UPDATE pending_sync_task 
        SET status = :status, 
            completed_on = :completedOn
        WHERE uid = :taskId
        """
    )
    suspend fun markAsCompleted(
        taskId: Long,
        status: SyncStatus = SyncStatus.COMPLETED,
        completedOn: LocalDateTime = LocalDateTime.now()
    )

    @Delete
    suspend fun deleteTask(task: PendingSyncTask)

    @Query("DELETE FROM pending_sync_task WHERE status = :status AND completed_on < :beforeDate")
    suspend fun deleteCompletedTasksOlderThan(status: SyncStatus, beforeDate: LocalDateTime)
}
