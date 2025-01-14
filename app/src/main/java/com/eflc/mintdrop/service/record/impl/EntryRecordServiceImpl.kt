package com.eflc.mintdrop.service.record.impl

import androidx.room.withTransaction
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.ExpenseEntryResponse
import com.eflc.mintdrop.models.SharedExpenseBalanceData
import com.eflc.mintdrop.models.SharedExpenseSplit
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
import com.eflc.mintdrop.room.dao.entity.SubcategoryMonthlyBalance
import com.eflc.mintdrop.service.record.EntryRecordService
import com.eflc.mintdrop.utils.Constants
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
    private val externalSheetRefRepository: ExternalSheetRefRepository
) : EntryRecordService {

    override suspend fun createRecord(entryRecord: EntryHistory, sheetName: String, paymentMethod: PaymentMethod?): ExpenseEntryResponse? {
        var expenseEntryResponse: ExpenseEntryResponse? = null
        db.withTransaction {
            entryHistoryRepository.saveEntryHistory(entryRecord)

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

            // Update Google sheet
            expenseEntryResponse = googleSheetsRepository.postExpense(
                buildExpenseEntryRequest(
                    row = row.rowNumber,
                    amount = entryRecord.amount,
                    description = entryRecord.description,
                    sheet = sheetName,
                    isOwedInstallments = false,
                    totalInstallments = 1,
                    paymentMethod = paymentMethod?.description ?: "",
                    month = monthValue,
                    spreadsheetId = spreadsheetId
                )
            )
        }

        return expenseEntryResponse
    }

    override suspend fun deleteRecord(entryRecord: EntryHistory) {
        db.withTransaction {
            val spreadsheetId = externalSheetRefRepository.findExternalSheetRefByYear(entryRecord.date.year)?.sheetId!!
            entryHistoryRepository.deleteEntryHistory(entryRecord)
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

    override suspend fun calculateSharedExpenseBalance(): SharedExpenseBalanceData {
        val pendingSharedExpenses = entryHistoryRepository.getPendingSharedExpenses()
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
