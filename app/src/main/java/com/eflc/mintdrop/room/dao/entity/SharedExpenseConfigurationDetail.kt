package com.eflc.mintdrop.room.dao.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "shared_expense_configuration_detail", foreignKeys = [
    ForeignKey(
        entity = SharedExpenseConfiguration::class,
        parentColumns = ["uid"],
        childColumns = ["configuration_id"],
        onDelete = ForeignKey.RESTRICT,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class SharedExpenseConfigurationDetail(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "configuration_id")
    val configurationId: Long,
    @ColumnInfo(name = "user_id")
    val userId: Long? = null,
    @ColumnInfo(name = "split_amount")
    val splitAmount: Double,
    @ColumnInfo(name = "split_amount_type")
    val splitAmountType: SplitAmountType,
)
