package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.SharedExpenseRepository
import com.eflc.mintdrop.room.dao.SharedExpenseConfigurationDao
import com.eflc.mintdrop.room.dao.SharedExpenseConfigurationDetailDao
import com.eflc.mintdrop.room.dao.SharedExpenseEntryDetailDao
import com.eflc.mintdrop.room.dao.SharedExpenseSettlementDao
import javax.inject.Inject

class SharedExpenseRepositoryImpl @Inject constructor(
    private val configurationDao: SharedExpenseConfigurationDao,
    private val configurationDetailDao: SharedExpenseConfigurationDetailDao,
    private val entryDetailDao: SharedExpenseEntryDetailDao,
    private val settlementDao: SharedExpenseSettlementDao
) : SharedExpenseRepository {

}
