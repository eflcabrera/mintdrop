package com.eflc.mintdrop.repository.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "entry_history", foreignKeys = [
    ForeignKey(
        entity = Subcategory::class,
        parentColumns = ["id"],
        childColumns = ["subcategoryId"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
])
data class EntryHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "subcategory_id")
    val subcategoryId: Int,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "date")
    val date: Date,
    @ColumnInfo(name = "created_on")
    val createdOn: Date,
    @ColumnInfo(name = "last_modified")
    val lastModified: Date?
)
