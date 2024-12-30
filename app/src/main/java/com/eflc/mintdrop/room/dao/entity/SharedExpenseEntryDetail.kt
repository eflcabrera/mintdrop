package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "shared_expense_entry_detail", foreignKeys = [
    ForeignKey(
        entity = EntryHistory::class,
        parentColumns = ["uid"],
        childColumns = ["entry_record_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = SharedExpenseSettlement::class,
        parentColumns = ["uid"],
        childColumns = ["settlement_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    ),
    // TODO: should probably go in entry_history
    ForeignKey(
        entity = SharedExpenseConfiguration::class,
        parentColumns = ["uid"],
        childColumns = ["shared_expense_configuration_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )
])
data class SharedExpenseEntryDetail(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "entry_record_id")
    val entryRecordId: Long,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "shared_expense_configuration_id")
    val sharedExpenseConfigurationId: Long,
    @ColumnInfo(name = "split")
    val split: Double,
    @ColumnInfo(name = "settlement_id")
    val settlementId: Long? = null,
)
