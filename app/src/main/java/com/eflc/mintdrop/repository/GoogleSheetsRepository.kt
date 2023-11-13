package com.eflc.mintdrop.repository

import com.eflc.mintdrop.api.GoogleSheetsAPI
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.GoogleSheetsResponse
import javax.inject.Inject

class GoogleSheetsRepository @Inject constructor(
    private val googleSheetsApi: GoogleSheetsAPI
) {
    suspend fun getCategories(spreadsheetId : String, sheet : String): GoogleSheetsResponse {
        return googleSheetsApi.getCategories(spreadsheetId, sheet)
    }

    suspend fun postExpense(expenseEntryRequest: ExpenseEntryRequest) {
        return googleSheetsApi.postExpense(expenseEntryRequest)
    }
}