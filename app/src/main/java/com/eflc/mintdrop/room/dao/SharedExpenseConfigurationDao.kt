package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.eflc.mintdrop.room.dao.entity.relationship.SharedExpenseConfigurationAndDetails
import java.time.LocalDateTime

@Dao
interface SharedExpenseConfigurationDao {

    @Query("SELECT * FROM shared_expense_configuration WHERE created_on < :date ORDER BY created_on DESC LIMIT 1")
    @Transaction
    fun getTopEntryOrderByDateDesc(date: LocalDateTime): SharedExpenseConfigurationAndDetails
}
