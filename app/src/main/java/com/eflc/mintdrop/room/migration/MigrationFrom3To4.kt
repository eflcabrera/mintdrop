package com.eflc.mintdrop.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom3To4 : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.apply {
            execSQL("""
                CREATE TABLE IF NOT EXISTS subcategory_monthly_balance (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `subcategory_id` INTEGER NOT NULL,
                    `month` INTEGER NOT NULL,
                    `year` INTEGER NOT NULL,
                    `balance` REAL NOT NULL,
                    `last_modified` TEXT,
                    FOREIGN KEY(`subcategory_id`) REFERENCES `subcategory`(`uid`) ON UPDATE CASCADE ON DELETE RESTRICT
                )
            """.trimIndent())
            execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_subcategory_monthly_balance_subcategory_id_month_year` ON `subcategory_monthly_balance` (`subcategory_id`, `month`, `year`)")
        }
    }
}
