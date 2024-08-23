package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.eflc.mintdrop.models.EntryType
import java.time.LocalDateTime

@Entity(
    tableName = "category",
    indices = [
        Index(value = ["name", "type"], unique = true)
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "external_id")
    val externalId: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "type")
    var type: EntryType,
    @ColumnInfo(name = "icon_ref")
    val iconRef: String? = "",
    @ColumnInfo(name = "created_on")
    val createdOn: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "last_modified")
    var lastModified: LocalDateTime? = null
)
