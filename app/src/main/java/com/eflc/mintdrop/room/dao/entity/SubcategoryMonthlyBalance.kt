package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "subcategory_monthly_balance", foreignKeys = [
    ForeignKey(
        entity = Subcategory::class,
        parentColumns = ["uid"],
        childColumns = ["subcategory_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
], indices = [
    Index(value = ["subcategory_id", "month", "year"], unique = true)
])
data class SubcategoryMonthlyBalance(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "subcategory_id")
    val subcategoryId: Long,
    @ColumnInfo(name = "month")
    val month: Int,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "balance")
    val balance: Double,
    @ColumnInfo(name = "last_modified")
    var lastModified: LocalDateTime? = null,
)
