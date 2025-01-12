package com.eflc.mintdrop.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom5To6: Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.apply {
            execSQL("""
                CREATE TABLE IF NOT EXISTS shared_expense_configuration (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `created_on` TEXT NOT NULL,
                    `type` TEXT NOT NULL,
                    `is_default` INTEGER NOT NULL
                )
            """.trimIndent())

            execSQL("""
                CREATE TABLE IF NOT EXISTS shared_expense_configuration_detail (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `configuration_id` INTEGER NOT NULL,
                    `user_id` INTEGER,
                    `split_amount` REAL NOT NULL,
                    `split_amount_type` TEXT NOT NULL,
                )
                FOREIGN KEY(`configuration_id`) REFERENCES `shared_expense_configuration`(`uid`) ON UPDATE CASCADE ON DELETE RESTRICT
            """.trimIndent())

            execSQL("""
                CREATE TABLE IF NOT EXISTS shared_expense_settlement (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `settlement_date` TEXT NOT NULL,
                    `amount` REAL NOT NULL,
                    `user_id` INTEGER,
                    `operation_type` TEXT NOT NULL,
                )
            """.trimIndent())

            execSQL("""
                CREATE TABLE IF NOT EXISTS shared_expense_entry_detail (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `entry_record_id` INTEGER NOT NULL,
                    `user_id` INTEGER NOT NULL,
                    `shared_expense_configuration_id` INTEGER NOT NULL,
                    `split` REAL NOT NULL,
                    `settlement_id` INTEGER,
                    FOREIGN KEY(`entry_record_id`) REFERENCES `entry_history`(`uid`) ON UPDATE CASCADE ON DELETE RESTRICT,
                    FOREIGN KEY(`shared_expense_configuration_id`) REFERENCES `shared_expense_configuration`(`uid`) ON UPDATE CASCADE ON DELETE RESTRICT,
                    FOREIGN KEY(`settlement_id`) REFERENCES `shared_expense_settlement`(`uid`) ON UPDATE CASCADE ON DELETE RESTRICT
                )
            """.trimIndent())

            execSQL("ALTER TABLE entry_history ADD COLUMN paid_by INTEGER")
            execSQL("ALTER TABLE entry_history ADD COLUMN is_settled INTEGER")
        }
    }
}
