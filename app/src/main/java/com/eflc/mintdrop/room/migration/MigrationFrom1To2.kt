package com.eflc.mintdrop.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom1To2 : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        // v2 agrega la columna is_shared a entry_history
        db.execSQL("ALTER TABLE entry_history ADD COLUMN `is_shared` INTEGER")
    }
}
