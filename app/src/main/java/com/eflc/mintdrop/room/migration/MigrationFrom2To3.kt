package com.eflc.mintdrop.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationFrom2To3 : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.apply {
            execSQL("CREATE TABLE IF NOT EXISTS payment_method (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `description` TEXT NOT NULL, `type` TEXT NOT NULL)")

            execSQL(
                "INSERT OR REPLACE INTO payment_method (uid, description, type) VALUES (1, 'Visa Crédito', 'CREDIT_CARD')"
            )
            execSQL(
                "INSERT OR REPLACE INTO payment_method (uid, description, type) VALUES (2, 'Master Crédito', 'CREDIT_CARD')"
            )
            execSQL(
                "INSERT OR REPLACE INTO payment_method (uid, description, type) VALUES (3, 'MercadoPago', 'DIGITAL_WALLET')"
            )

            execSQL("""
                CREATE TABLE IF NOT EXISTS entry_history_new (
                    `uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `subcategory_id` INTEGER NOT NULL,
                    `amount` REAL NOT NULL,
                    `description` TEXT NOT NULL,
                    `date` TEXT NOT NULL,
                    `created_on` TEXT NOT NULL,
                    `last_modified` TEXT,
                    `is_shared` INTEGER,
                    `payment_method_id` INTEGER,
                    FOREIGN KEY(`subcategory_id`) REFERENCES `subcategory`(`uid`) ON UPDATE CASCADE ON DELETE RESTRICT,
                    FOREIGN KEY(`payment_method_id`) REFERENCES `payment_method`(`uid`) ON UPDATE CASCADE ON DELETE RESTRICT
                )
            """.trimIndent())

            execSQL("INSERT INTO entry_history_new SELECT uid, subcategory_id, amount, description, date, created_on, last_modified, is_shared, NULL as payment_method_id FROM entry_history")
            execSQL("DROP TABLE entry_history")
            execSQL("ALTER TABLE entry_history_new RENAME TO entry_history")
        }
    }
}
