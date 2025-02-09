package com.eflc.mintdrop.service.shared.impl

import androidx.room.withTransaction
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.SharedExpenseRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.SharedExpenseConfigType
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.room.dao.entity.SharedExpenseSettlement
import com.eflc.mintdrop.room.dao.entity.TransferOperationType
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import com.eflc.mintdrop.room.dao.entity.relationship.SharedExpenseConfigurationAndDetails
import com.eflc.mintdrop.service.shared.SharedExpenseService
import com.eflc.mintdrop.utils.Constants
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.absoluteValue

class SharedExpenseServiceImpl @Inject constructor(
    private val db: JulepDatabase,
    private val sharedExpenseRepository: SharedExpenseRepository,
    private val entryHistoryRepository: EntryHistoryRepository
) : SharedExpenseService {

    override suspend fun createSharedExpenseEntries(sharedEntryRecord: EntryHistory, entryRecordId: Long): List<SharedExpenseEntryDetail> {
        return db.withTransaction {
            val config = sharedExpenseRepository.findSharedExpenseConfigurationForDate(sharedEntryRecord.date)
            val splitMap = calculateSplits(sharedEntryRecord.amount, config)
            val entryDetails = mutableListOf<SharedExpenseEntryDetail>()

            config.details.forEach { detail ->
                val sharedExpenseEntryDetail = detail.userId?.let {
                    SharedExpenseEntryDetail(
                        entryRecordId = entryRecordId,
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

    override suspend fun deleteSharedExpenseEntries(sharedEntryRecord: EntryHistory) {
        sharedExpenseRepository.deleteSharedExpenseEntryDetailsByEntryRecordId(sharedEntryRecord.uid)
    }

    override suspend fun createBalanceSettlement(
        balance: Double,
        pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>
    ): EntryHistory {
        return db.withTransaction {
            val operationType: TransferOperationType
            val settlementEntrySubcategoryId: Long

            if (balance > 0.0) {
                operationType = TransferOperationType.CREDIT
                settlementEntrySubcategoryId = Constants.DEFAULT_SETTLE_CREDIT_SUBCAT
            } else {
                operationType = TransferOperationType.DEBIT
                settlementEntrySubcategoryId = Constants.DEFAULT_SETTLE_DEBIT_SUBCAT
            }

            val settlement = SharedExpenseSettlement(
                settlementDate = LocalDateTime.now(),
                amount = balance.absoluteValue,
                type = operationType,
                userId = Constants.MY_USER_ID
            )

            val settlementId = sharedExpenseRepository.saveSharedExpenseSettlement(settlement)

            pendingSharedExpenses.forEach { recordAndSharedExpenses ->
                recordAndSharedExpenses.entryRecord.isSettled = true
                recordAndSharedExpenses.entryRecord.lastModified = LocalDateTime.now()
                recordAndSharedExpenses.sharedExpenseDetails.forEach {
                    it.settlementId = settlementId
                    sharedExpenseRepository.saveSharedExpenseEntryDetail(it)
                }
                entryHistoryRepository.saveEntryHistory(recordAndSharedExpenses.entryRecord)
            }

            return@withTransaction EntryHistory(
                subcategoryId = settlementEntrySubcategoryId,
                amount = balance.absoluteValue,
                description = "Saldo de balance",
                lastModified = LocalDateTime.now(),
                isShared = false,
                date = LocalDateTime.now()
            )
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
