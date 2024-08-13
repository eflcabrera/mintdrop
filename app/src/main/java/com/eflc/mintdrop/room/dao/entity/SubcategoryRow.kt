package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "subcategory_row", foreignKeys = [
    ForeignKey(
        entity = Subcategory::class,
        parentColumns = ["uid"],
        childColumns = ["subcategory_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
])
data class SubcategoryRow(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "subcategory_id")
    val subcategoryId: Long,
    @ColumnInfo(name = "row_number")
    val rowNumber: Int,
)
