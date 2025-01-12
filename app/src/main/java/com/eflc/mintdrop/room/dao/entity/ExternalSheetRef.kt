package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "external_sheet_ref")
data class ExternalSheetRef (
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "sheet_id")
    var sheetId: String
)
