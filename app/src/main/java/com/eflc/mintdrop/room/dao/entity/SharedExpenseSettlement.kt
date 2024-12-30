package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "shared_expense_settlement", foreignKeys = [
    ForeignKey(
        entity = EntryRecord::class,
        parentColumns = ["uid"],
        childColumns = ["entry_record_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
])
data class SharedExpenseSettlement(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "settlement_date")
    val settlementDate: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "user_id")
    val userId: Long? = null,
    @ColumnInfo(name = "type")
    val type: TransferOperationType
)
