package com.eflc.mintdrop.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom4To5 : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.apply {
            execSQL("""
                CREATE TABLE IF NOT EXISTS external_sheet_ref (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `year` INTEGER NOT NULL,
                    `sheet_id` TEXT NOT NULL
                )
            """.trimIndent())
        }
    }
}
