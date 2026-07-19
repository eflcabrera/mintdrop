package com.eflc.mintdrop.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom6To7 : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.apply {
            // Entradas históricas ya estaban en el Sheet: evitar falso "pendiente"
            execSQL("ALTER TABLE entry_history ADD COLUMN synced_to_sheets INTEGER NOT NULL DEFAULT 1")

            execSQL(
                """
                CREATE TABLE IF NOT EXISTS pending_sync_task (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `operation_id` TEXT NOT NULL,
                    `entry_history_id` INTEGER,
                    `task_type` TEXT NOT NULL,
                    `payload` TEXT NOT NULL,
                    `status` TEXT NOT NULL,
                    `attempt_count` INTEGER NOT NULL DEFAULT 0,
                    `max_attempts` INTEGER NOT NULL DEFAULT 5,
                    `created_on` TEXT NOT NULL,
                    `last_attempt_on` TEXT,
                    `error_message` TEXT,
                    `completed_on` TEXT
                )
                """.trimIndent()
            )

            execSQL(
                "CREATE INDEX IF NOT EXISTS index_pending_sync_task_status_attempt_count " +
                    "ON pending_sync_task(status, attempt_count)"
            )
            execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_pending_sync_task_operation_id " +
                    "ON pending_sync_task(operation_id)"
            )
            execSQL(
                "CREATE INDEX IF NOT EXISTS index_pending_sync_task_entry_history_id " +
                    "ON pending_sync_task(entry_history_id)"
            )
        }
    }
}
