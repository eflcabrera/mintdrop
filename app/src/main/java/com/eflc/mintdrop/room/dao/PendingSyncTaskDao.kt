package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.eflc.mintdrop.room.dao.entity.PendingSyncTask
import com.eflc.mintdrop.room.dao.entity.SyncStatus
import java.time.LocalDateTime

@Dao
interface PendingSyncTaskDao {
    @Upsert
    suspend fun insertOrUpdateTask(task: PendingSyncTask): Long
    
    @Query("SELECT * FROM pending_sync_task WHERE status = :status ORDER BY created_on ASC LIMIT :limit")
    suspend fun getTasksByStatus(status: SyncStatus, limit: Int = 10): List<PendingSyncTask>
    
    @Query("""
        SELECT * FROM pending_sync_task 
        WHERE (status = :pendingStatus OR (status = :failedStatus AND attempt_count < max_attempts))
        ORDER BY created_on ASC 
        LIMIT :limit
    """)
    suspend fun getPendingTasks(
        pendingStatus: SyncStatus = SyncStatus.PENDING,
        failedStatus: SyncStatus = SyncStatus.FAILED,
        limit: Int = 10
    ): List<PendingSyncTask>
    
    @Query("SELECT * FROM pending_sync_task WHERE uid = :taskId")
    suspend fun getTaskById(taskId: Long): PendingSyncTask?
    
    @Update
    suspend fun updateTask(task: PendingSyncTask)
    
    @Query("""
        UPDATE pending_sync_task 
        SET status = :status, 
            attempt_count = :attemptCount, 
            last_attempt_on = :lastAttemptOn,
            error_message = :errorMessage
        WHERE uid = :taskId
    """)
    suspend fun updateTaskStatus(
        taskId: Long,
        status: SyncStatus,
        attemptCount: Int,
        lastAttemptOn: LocalDateTime?,
        errorMessage: String?
    )
    
    @Query("""
        UPDATE pending_sync_task 
        SET status = :status, 
            completed_on = :completedOn
        WHERE uid = :taskId
    """)
    suspend fun markAsCompleted(taskId: Long, status: SyncStatus = SyncStatus.COMPLETED, completedOn: LocalDateTime = LocalDateTime.now())
    
    @Delete
    suspend fun deleteTask(task: PendingSyncTask)
    
    @Query("DELETE FROM pending_sync_task WHERE status = :status AND completed_on < :beforeDate")
    suspend fun deleteCompletedTasksOlderThan(status: SyncStatus, beforeDate: LocalDateTime)
}

