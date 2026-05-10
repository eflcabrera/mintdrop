package com.eflc.mintdrop.backup

import android.app.backup.BackupAgentHelper
import android.app.backup.FileBackupHelper
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.os.ParcelFileDescriptor
import android.util.Log

/**
 * BackupAgent que garantiza consistencia del archivo WAL antes de que Android
 * copie la base de datos al cloud backup.
 *
 * Sin este checkpoint, Android podría respaldar julep.db con el WAL sin vaciar,
 * lo que resultaría en una DB corrupta o incompleta al restaurar.
 */
class JulepBackupAgent : BackupAgentHelper() {

    companion object {
        private const val TAG = "JulepBackupAgent"
        private const val DB_NAME = "julep.db"
        private const val BACKUP_KEY = "julep_db"
    }

    override fun onCreate() {
        addHelper(BACKUP_KEY, FileBackupHelper(this, "../databases/$DB_NAME"))
    }

    override fun onBackup(
        oldState: ParcelFileDescriptor?,
        data: BackupDataOutput?,
        newState: ParcelFileDescriptor?
    ) {
        // Vaciar el WAL antes de que Android lea el archivo para evitar DB corrupta
        flushWal()
        super.onBackup(oldState, data, newState)
    }

    override fun onRestore(
        data: BackupDataInput?,
        appVersionCode: Int,
        newState: ParcelFileDescriptor?
    ) {
        super.onRestore(data, appVersionCode, newState)
        Log.i(TAG, "Base de datos restaurada desde backup")
    }

    private fun flushWal() {
        try {
            val dbFile = getDatabasePath(DB_NAME)
            if (!dbFile.exists()) return

            // Abrir conexión de solo lectura para ejecutar el checkpoint sin Hilt
            val db = android.database.sqlite.SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                android.database.sqlite.SQLiteDatabase.OPEN_READWRITE
            )
            db.use { it.execSQL("PRAGMA wal_checkpoint(TRUNCATE)") }
            Log.d(TAG, "WAL checkpoint ejecutado correctamente")
        } catch (e: Exception) {
            // No interrumpir el backup si el checkpoint falla; mejor un backup
            // potencialmente incompleto que ninguno
            Log.w(TAG, "No se pudo ejecutar WAL checkpoint: ${e.message}")
        }
    }
}
