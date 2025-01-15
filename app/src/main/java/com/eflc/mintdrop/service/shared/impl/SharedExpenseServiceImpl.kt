package com.eflc.mintdrop.service.shared.impl

import androidx.room.withTransaction
import com.eflc.mintdrop.repository.SharedExpenseRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.SharedExpenseConfigType
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.room.dao.entity.relationship.SharedExpenseConfigurationAndDetails
import com.eflc.mintdrop.service.shared.SharedExpenseService
import javax.inject.Inject

class SharedExpenseServiceImpl @Inject constructor(
    private val db: JulepDatabase,
    private val sharedExpenseRepository: SharedExpenseRepository,
) : SharedExpenseService {

    override suspend fun createSharedExpenseEntries(sharedEntryRecord: EntryHistory): List<SharedExpenseEntryDetail> {
        return db.withTransaction {
            val config = sharedExpenseRepository.findSharedExpenseConfigurationForDate(sharedEntryRecord.date)
            val splitMap = calculateSplits(sharedEntryRecord.amount, config)
            val entryDetails = mutableListOf<SharedExpenseEntryDetail>()

            config.details.forEach { detail ->
                val sharedExpenseEntryDetail = detail.userId?.let {
                    SharedExpenseEntryDetail(
                        entryRecordId = sharedEntryRecord.uid,
                        userId = it,
                        sharedExpenseConfigurationId = config.configuration.uid,
                        split = splitMap[it]!!
                    )
                }
                if (sharedExpenseEntryDetail != null) {
                    entryDetails.add(sharedExpenseEntryDetail)
                    sharedExpenseRepository.saveSharedExpenseEntryDetail(sharedExpenseEntryDetail)
                }
            }

            return@withTransaction entryDetails
        }
    }

    // Move this to a strategy pattern
    private fun calculateSplits(amount: Double, config: SharedExpenseConfigurationAndDetails): Map<Long, Double> {
        val splitMap = mutableMapOf<Long, Double>()
        if (SharedExpenseConfigType.SHARES == config.configuration.type) {
            val totalShares = config.details.sumOf { it.splitAmount }
            val shareUnit = amount.div(totalShares)
            config.details.forEach { detail ->
                detail.userId?.let { splitMap[it] = shareUnit.times(detail.splitAmount) }
            }
        }
        return splitMap
    }

}
