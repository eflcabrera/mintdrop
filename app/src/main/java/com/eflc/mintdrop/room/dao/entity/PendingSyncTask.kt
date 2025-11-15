package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

enum class SyncTaskType {
    EXPENSE_ENTRY,
    CATEGORY_MANAGEMENT
}

enum class SyncStatus {
    PENDING,
    IN_PROGRESS,
    FAILED,
    COMPLETED
}

@Entity(
    tableName = "pending_sync_task",
    indices = [
        Index(value = ["status", "attempt_count"]),
    ]
)
data class PendingSyncTask(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    
    @ColumnInfo(name = "task_type")
    val taskType: SyncTaskType,
    
    @ColumnInfo(name = "payload")
    val payload: String,  // JSON serializado con ExpenseEntryRequest + entryHistoryId
    
    @ColumnInfo(name = "status")
    val status: SyncStatus,
    
    @ColumnInfo(name = "attempt_count")
    val attemptCount: Int = 0,
    
    @ColumnInfo(name = "max_attempts")
    val maxAttempts: Int = 3,
    
    @ColumnInfo(name = "created_on")
    val createdOn: LocalDateTime = LocalDateTime.now(),
    
    @ColumnInfo(name = "last_attempt_on")
    val lastAttemptOn: LocalDateTime? = null,
    
    @ColumnInfo(name = "error_message")
    val errorMessage: String? = null,
    
    @ColumnInfo(name = "completed_on")
    val completedOn: LocalDateTime? = null
)


