package com.eflc.mintdrop.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom6To7 : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.apply {
            // Agregar columna synced_to_sheets a entry_history
            execSQL("ALTER TABLE entry_history ADD COLUMN synced_to_sheets INTEGER NOT NULL DEFAULT 0")
            
            // Crear tabla pending_sync_task
            execSQL("""
                CREATE TABLE IF NOT EXISTS pending_sync_task (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `task_type` TEXT NOT NULL,
                    `payload` TEXT NOT NULL,
                    `status` TEXT NOT NULL,
                    `attempt_count` INTEGER NOT NULL DEFAULT 0,
                    `max_attempts` INTEGER NOT NULL DEFAULT 3,
                    `created_on` TEXT NOT NULL,
                    `last_attempt_on` TEXT,
                    `error_message` TEXT,
                    `completed_on` TEXT
                )
            """.trimIndent())
            
            // Crear índices
            execSQL("CREATE INDEX IF NOT EXISTS index_pending_sync_task_status_attempt_count ON pending_sync_task(status, attempt_count)")
        }
    }
}

