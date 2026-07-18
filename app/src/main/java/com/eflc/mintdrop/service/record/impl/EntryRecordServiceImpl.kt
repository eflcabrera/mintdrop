package com.eflc.mintdrop.service.record.impl

import android.util.Log
import androidx.room.withTransaction
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.models.SharedExpenseSplit
import com.eflc.mintdrop.models.SyncPayload
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import com.eflc.mintdrop.service.record.EntryRecordService
import com.eflc.mintdrop.service.shared.SharedExpenseService
import com.eflc.mintdrop.service.sync.OutboxEnqueuer
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.Constants.MY_USER_ID
import java.time.LocalDateTime
import javax.inject.Inject

class EntryRecordServiceImpl @Inject constructor(
    private val db: JulepDatabase,
    private val entryHistoryRepository: EntryHistoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val categoryRepository: CategoryRepository,
    private val subcategoryRowRepository: SubcategoryRowRepository,
    private val subcategoryMonthlyBalanceRepository: SubcategoryMonthlyBalanceRepository,
    private val externalSheetRefRepository: ExternalSheetRefRepository,
    private val sharedExpenseService: SharedExpenseService,
    private val outboxEnqueuer: OutboxEnqueuer
) : EntryRecordService {

    override suspend fun createRecord(
        entryRecord: EntryHistory,
        sheetName: String,
        paymentMethod: PaymentMethod?
    ): ExpenseEntryResponse? {
        var taskIdToSchedule: Long? = null

        db.withTransaction {
            val entryRecordId = entryHistoryRepository.saveEntryHistory(entryRecord)

            if (entryRecord.isShared == true) {
                sharedExpenseService.createSharedExpenseEntries(entryRecord, entryRecordId)

                if (entryRecord.paidBy != null && entryRecord.paidBy != MY_USER_ID) {
                    // No se postea al Sheet de este usuario → no mostrar PENDING eterno
                    db.entryHistoryDao.markAsSyncedToSheets(entryRecordId)
                    return@withTransaction
                }
            }

            val yearValue = entryRecord.date.year
            val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(yearValue)?.sheetId!!
            val monthValue = entryRecord.date.monthValue
            val subcategory = subcategoryRepository.findSubcategoryById(entryRecord.subcategoryId)
            val row = subcategoryRowRepository.findRowBySubcategoryId(subcategory.uid)
            val subcategoryBalance = subcategoryMonthlyBalanceRepository.findBalanceBySubcategoryIdAndPeriod(
                subcategory.uid, yearValue, monthValue
            ) ?: SubcategoryMonthlyBalance(
                subcategoryId = subcategory.uid,
                month = monthValue,
                year = yearValue,
                balance = 0.0
            )

            subcategory.lastEntryOn = LocalDateTime.now()
            subcategory.lastModified = LocalDateTime.now()
            subcategoryRepository.saveSubcategory(subcategory)

            subcategoryBalance.balance += entryRecord.amount
            subcategoryBalance.lastModified = LocalDateTime.now()
            subcategoryMonthlyBalanceRepository.saveSubcategoryMonthlyBalance(subcategoryBalance)

            val syncPayload = SyncPayload(
                operationId = outboxEnqueuer.newOperationId(),
                entryHistoryId = entryRecordId,
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                month = monthValue,
                row = row.rowNumber,
                amount = entryRecord.amount,
                description = entryRecord.description,
                isOwedInstallments = false,
                totalInstallments = 1,
                paymentMethod = paymentMethod?.description ?: ""
            )

            taskIdToSchedule = outboxEnqueuer.enqueue(syncPayload)
            Log.d("EntryRecordService", "Outbox encolado: taskId=$taskIdToSchedule, entryHistoryId=$entryRecordId")
        }

        taskIdToSchedule?.let { outboxEnqueuer.scheduleSync(it) }
        return null
    }

    override suspend fun deleteRecord(entryRecord: EntryHistory) {
        var taskIdToSchedule: Long? = null

        db.withTransaction {
            if (entryRecord.isShared == true) {
                sharedExpenseService.deleteSharedExpenseEntries(entryRecord)
            }

            entryHistoryRepository.deleteEntryHistory(entryRecord)

            if (entryRecord.isShared == true && entryRecord.paidBy != null && entryRecord.paidBy != MY_USER_ID) {
                return@withTransaction
            }

            val subcategory = subcategoryRepository.findSubcategoryById(entryRecord.subcategoryId)
            val currentBalance =
                subcategoryMonthlyBalanceRepository.findBalanceBySubcategoryIdAndPeriod(
                    subcategory.uid, entryRecord.date.year, entryRecord.date.monthValue
                )

            if (currentBalance != null) {
                currentBalance.balance = currentBalance.balance.minus(entryRecord.amount)
                subcategoryMonthlyBalanceRepository.saveSubcategoryMonthlyBalance(currentBalance)
            }

            // Si aún no se marcó synced: cancelar create pendiente/en vuelo.
            // UNDO desde acá solo si hubo CREATE COMPLETED previo; si estaba IN_PROGRESS,
            // SyncWorker encola el compensatorio tras un POST que ya impactó el Sheet.
            if (!entryRecord.syncedToSheets) {
                val needsCompensatingUndo = outboxEnqueuer.cancelActiveTasksForEntry(entryRecord.uid)
                if (!needsCompensatingUndo) {
                    return@withTransaction
                }
            }

            val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(entryRecord.date.year)?.sheetId!!
            val row = subcategoryRowRepository.findRowBySubcategoryId(subcategory.uid)
            val cat = categoryRepository.findCategoryById(subcategory.categoryId)
            val sheetName =
                if (cat.category.type == EntryType.EXPENSE) Constants.EXPENSE_SHEET_NAME
                else Constants.INCOME_SHEET_NAME

            taskIdToSchedule = outboxEnqueuer.enqueueUndoIfAbsent(
                entryHistoryId = entryRecord.uid,
                spreadsheetId = spreadsheetId,
                sheetName = sheetName,
                month = entryRecord.date.monthValue,
                row = row.rowNumber,
                originalAmount = entryRecord.amount,
                originalDescription = entryRecord.description
            )
        }

        taskIdToSchedule?.let { outboxEnqueuer.scheduleSync(it) }
    }

    override suspend fun calculateSharedExpenseBalance(
        pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>
    ): SharedExpenseBalanceData {
        val sharedExpenseSplits: MutableList<SharedExpenseSplit> = ArrayList()
        val sharedExpenseBalanceData = SharedExpenseBalanceData(0.0, sharedExpenseSplits)

        pendingSharedExpenses.forEach { pendingExpense ->
            sharedExpenseBalanceData.total += pendingExpense.entryRecord.amount
            pendingExpense.sharedExpenseDetails.forEach { sharedExpenseDetail ->
                val owed = sharedExpenseDetail.split
                var paid = 0.0

                if (sharedExpenseDetail.userId == pendingExpense.entryRecord.paidBy) {
                    paid = pendingExpense.entryRecord.amount
                }

                val split = sharedExpenseSplits.find { sharedExpenseDetail.userId == it.userId }
                if (split == null) {
                    sharedExpenseSplits.add(
                        SharedExpenseSplit(sharedExpenseDetail.userId, owed, paid)
                    )
                } else {
                    split.owed += owed
                    split.paid += paid
                }
            }
        }
        return sharedExpenseBalanceData
    }

    override suspend fun getPendingSharedExpenses(): List<EntryRecordAndSharedExpenseDetails> {
        return entryHistoryRepository.getPendingSharedExpenses()
    }

    override suspend fun settleSharedExpenseBalance(
        balance: Double,
        pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>
    ): ExpenseEntryResponse? {
        // createBalanceSettlement ya usa withTransaction; createRecord encola post-commit.
        // No anidar createRecord dentro de otra withTransaction (evita schedule antes del commit).
        val sheetName = if (balance > 0.0) Constants.INCOME_SHEET_NAME else Constants.EXPENSE_SHEET_NAME
        val settlementEntry = sharedExpenseService.createBalanceSettlement(balance, pendingSharedExpenses)
        return createRecord(settlementEntry, sheetName, null)
    }

    override suspend fun retryFailedSync(entryHistoryId: Long): Boolean {
        return outboxEnqueuer.retryFailedTask(entryHistoryId) != null
    }
}
