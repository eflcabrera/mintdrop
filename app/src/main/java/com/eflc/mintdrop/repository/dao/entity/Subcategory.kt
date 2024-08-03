package com.eflc.mintdrop.repository.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "subcategory", foreignKeys = [
    ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
])
data class Subcategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    @ColumnInfo(name = "external_id")
    val externalId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "icon_ref")
    val iconRef: String,
    @ColumnInfo(name = "last_entry_on")
    val lastEntryOn: Date?,
    @ColumnInfo(name = "created_on")
    val createdOn: Date,
    @ColumnInfo(name = "last_modified")
    val lastModified: Date?
)
