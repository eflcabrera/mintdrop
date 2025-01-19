package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "entry_history", foreignKeys = [
    ForeignKey(
        entity = Subcategory::class,
        parentColumns = ["uid"],
        childColumns = ["subcategory_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = PaymentMethod::class,
        parentColumns = ["uid"],
        childColumns = ["payment_method_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
])
data class EntryHistory(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "subcategory_id")
    val subcategoryId: Long,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "date")
    val date: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "created_on")
    val createdOn: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "last_modified")
    val lastModified: LocalDateTime?,
    @ColumnInfo(name = "is_shared")
    val isShared: Boolean? = false,
    @ColumnInfo(name = "payment_method_id")
    val paymentMethodId: Long? = null,
    @ColumnInfo(name = "paid_by")
    val paidBy: Long? = null,
    @ColumnInfo(name = "is_settled")
    var isSettled: Boolean? = null
)
