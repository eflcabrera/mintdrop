package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.SharedExpenseRepository
import com.eflc.mintdrop.room.dao.SharedExpenseConfigurationDao
import com.eflc.mintdrop.room.dao.SharedExpenseConfigurationDetailDao
import com.eflc.mintdrop.room.dao.SharedExpenseEntryDetailDao
import com.eflc.mintdrop.room.dao.SharedExpenseSettlementDao
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.room.dao.entity.relationship.SharedExpenseConfigurationAndDetails
import java.time.LocalDateTime
import javax.inject.Inject

class SharedExpenseRepositoryImpl @Inject constructor(
    private val configurationDao: SharedExpenseConfigurationDao,
    private val configurationDetailDao: SharedExpenseConfigurationDetailDao,
    private val entryDetailDao: SharedExpenseEntryDetailDao,
    private val settlementDao: SharedExpenseSettlementDao
) : SharedExpenseRepository {
    override suspend fun saveSharedExpenseEntryDetail(sharedExpenseEntryDetail: SharedExpenseEntryDetail): Long {
        return entryDetailDao.saveSharedExpenseEntryDetail(sharedExpenseEntryDetail)
    }

    override suspend fun findSharedExpenseConfigurationForDate(date: LocalDateTime): SharedExpenseConfigurationAndDetails {
        return configurationDao.getTopEntryOrderByDateDesc(date)
    }

    override suspend fun deleteSharedExpenseEntryDetailsByEntryRecordId(entryRecordId: Long) {
        entryDetailDao.deleteAllByEntryRecordId(entryRecordId)
    }
}
