package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "external_id")
    val externalId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "icon_ref")
    val iconRef: String,
    @ColumnInfo(name = "created_on")
    val createdOn: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "last_modified")
    val lastModified: LocalDateTime?
)
