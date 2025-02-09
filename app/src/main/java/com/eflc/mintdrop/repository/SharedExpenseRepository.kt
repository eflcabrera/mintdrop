package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.room.dao.entity.SharedExpenseSettlement
import com.eflc.mintdrop.room.dao.entity.relationship.SharedExpenseConfigurationAndDetails
import java.time.LocalDateTime

interface SharedExpenseRepository {
    suspend fun saveSharedExpenseEntryDetail(sharedExpenseEntryDetail: SharedExpenseEntryDetail): Long
    suspend fun findSharedExpenseConfigurationForDate(date: LocalDateTime): SharedExpenseConfigurationAndDetails
    suspend fun deleteSharedExpenseEntryDetailsByEntryRecordId(entryRecordId: Long)
    suspend fun saveSharedExpenseSettlement(sharedExpenseSettlement: SharedExpenseSettlement): Long
}
