package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "subcategory", foreignKeys = [
    ForeignKey(
        entity = Category::class,
        parentColumns = ["uid"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
])
data class Subcategory(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "external_id")
    val externalId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "icon_ref")
    val iconRef: String? = "",
    @ColumnInfo(name = "created_on")
    val createdOn: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "last_modified")
    val lastModified: LocalDateTime? = null,
    @ColumnInfo(name = "last_entry_on")
    val lastEntryOn: LocalDateTime? = null
)
