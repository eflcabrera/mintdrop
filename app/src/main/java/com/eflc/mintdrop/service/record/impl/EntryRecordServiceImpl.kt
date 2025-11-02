package com.eflc.mintdrop.service.record.impl

import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.room.withTransaction
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.models.SharedExpenseSplit
import com.eflc.mintdrop.models.SyncPayload
import com.eflc.mintdrop.repository.CategoryRepository
import com.eflc.mintdrop.repository.EntryHistoryRepository
import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.repository.GoogleSheetsRepository
import com.eflc.mintdrop.repository.SubcategoryMonthlyBalanceRepository
import com.eflc.mintdrop.repository.SubcategoryRepository
import com.eflc.mintdrop.repository.SubcategoryRowRepository
import com.eflc.mintdrop.room.JulepDatabase
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import com.eflc.mintdrop.room.dao.entity.PendingSyncTask
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance
import com.eflc.mintdrop.room.dao.entity.SyncStatus
import com.eflc.mintdrop.room.dao.entity.SyncTaskType
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails
import com.eflc.mintdrop.service.record.EntryRecordService
import com.eflc.mintdrop.service.shared.SharedExpenseService
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.Constants.MY_USER_ID
import com.eflc.mintdrop.worker.SyncWorker
import com.squareup.moshi.Moshi
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class EntryRecordServiceImpl @Inject constructor(
    private val db: JulepDatabase,
    private val entryHistoryRepository: EntryHistoryRepository,
    private val subcategoryRepository: SubcategoryRepository,
    private val categoryRepository: CategoryRepository,
    private val subcategoryRowRepository: SubcategoryRowRepository,
    private val subcategoryMonthlyBalanceRepository: SubcategoryMonthlyBalanceRepository,
    private val googleSheetsRepository: GoogleSheetsRepository,
    private val externalSheetRefRepository: ExternalSheetRefRepository,
    private val sharedExpenseService: SharedExpenseService,
    private val workManager: WorkManager,
    private val moshi: Moshi
) : EntryRecordService {

    override suspend fun createRecord(entryRecord: EntryHistory, sheetName: String, paymentMethod: PaymentMethod?): ExpenseEntryResponse? {
        db.withTransaction {
            val entryRecordId = entryHistoryRepository.saveEntryHistory(entryRecord)

            // Generate shared expense entries if applicable
            if (entryRecord.isShared == true) {
                sharedExpenseService.createSharedExpenseEntries(entryRecord, entryRecordId)

                if (entryRecord.paidBy != null && entryRecord.paidBy != MY_USER_ID) {
                    return@withTransaction null
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

            // Update subcategory
            subcategory.lastEntryOn = LocalDateTime.now()
            subcategory.lastModified = LocalDateTime.now()
            subcategoryRepository.saveSubcategory(subcategory)

            // Update subcategory balance
            subcategoryBalance.balance += entryRecord.amount
            subcategoryBalance.lastModified = LocalDateTime.now()
            subcategoryMonthlyBalanceRepository.saveSubcategoryMonthlyBalance(subcategoryBalance)

            // Crear tarea de sincronización en lugar de llamar directamente a la API
            val syncPayload = SyncPayload(
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

            // Serializar payload a JSON
            val payloadJsonAdapter = moshi.adapter(SyncPayload::class.java)
            val payloadJson = payloadJsonAdapter.toJson(syncPayload)

            // Crear PendingSyncTask
            val pendingSyncTask = PendingSyncTask(
                taskType = SyncTaskType.EXPENSE_ENTRY,
                payload = payloadJson,
                status = SyncStatus.PENDING
            )

            val taskId = db.pendingSyncTaskDao.insertOrUpdateTask(pendingSyncTask)

            // Encolar WorkManager para procesar en segundo plano
            val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInputData(SyncWorker.createInputData(taskId))
                .addTag("sync_expense_entry")
                .build()

            workManager.enqueue(syncWorkRequest)

            Log.d("EntryRecordService", "Tarea de sincronización creada: taskId=$taskId, entryHistoryId=$entryRecordId")
        }

        // Retornar null porque la sincronización se hará en segundo plano
        // El usuario puede continuar sin esperar
        return null
    }

    override suspend fun deleteRecord(entryRecord: EntryHistory) {
        db.withTransaction {
            if (entryRecord.isShared == true) {
                sharedExpenseService.deleteSharedExpenseEntries(entryRecord)
            }

            entryHistoryRepository.deleteEntryHistory(entryRecord)

            if (entryRecord.isShared == true && entryRecord.paidBy != null && entryRecord.paidBy != MY_USER_ID) {
                return@withTransaction
            }

            val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(entryRecord.date.year)?.sheetId!!
            val subcategory = subcategoryRepository.findSubcategoryById(entryRecord.subcategoryId)
            val currentBalance =
                subcategoryMonthlyBalanceRepository.findBalanceBySubcategoryIdAndPeriod(
                    subcategory.uid, entryRecord.date.year, entryRecord.date.monthValue
                )

            if (currentBalance != null) {
                currentBalance.balance = currentBalance.balance.minus(entryRecord.amount)
                subcategoryMonthlyBalanceRepository.saveSubcategoryMonthlyBalance(currentBalance)
            }

            val row = subcategoryRowRepository.findRowBySubcategoryId(subcategory.uid)
            val cat = categoryRepository.findCategoryById(subcategory.categoryId)
            googleSheetsRepository.postExpense(
                buildExpenseEntryRequest(
                    row = row.rowNumber,
                    amount = -1 * entryRecord.amount,
                    description = "UNDO ${entryRecord.description}",
                    sheet = if (cat.category.type == EntryType.EXPENSE) Constants.EXPENSE_SHEET_NAME else Constants.INCOME_SHEET_NAME,
                    isOwedInstallments = false,
                    totalInstallments = 1,
                    paymentMethod = "",
                    month = entryRecord.date.monthValue,
                    spreadsheetId = spreadsheetId
                )
            )
        }
    }

    override suspend fun calculateSharedExpenseBalance(pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>): SharedExpenseBalanceData {
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

    override suspend fun settleSharedExpenseBalance(balance: Double, pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>): ExpenseEntryResponse? {
        return db.withTransaction {
            val sheetName = if (balance > 0.0) Constants.INCOME_SHEET_NAME else Constants.EXPENSE_SHEET_NAME
            val settlementEntry = sharedExpenseService.createBalanceSettlement(balance, pendingSharedExpenses)
            return@withTransaction createRecord(settlementEntry, sheetName, null)
        }
    }

    private fun buildExpenseEntryRequest(
        row: Int,
        amount: Double,
        description: String = "",
        month: Int = LocalDate.now().monthValue,
        sheet: String,
        isOwedInstallments: Boolean,
        totalInstallments: Int,
        paymentMethod: String,
        spreadsheetId: String
    ): ExpenseEntryRequest {
        return ExpenseEntryRequest(
            spreadsheetId = spreadsheetId,
            sheetName = sheet,
            month = month,
            amount = amount,
            description = description,
            row = row,
            isOwedInstallments = isOwedInstallments,
            totalInstallments = totalInstallments,
            paymentMethod = paymentMethod
        )
    }
}
