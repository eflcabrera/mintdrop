package com.eflc.mintdrop.repository

import com.eflc.mintdrop.api.GoogleSheetsAPI
import com.eflc.mintdrop.models.CategoriesResponse
import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.ExpenseEntryResponse
import javax.inject.Inject

class GoogleSheetsRepository @Inject constructor(
    private val googleSheetsApi: GoogleSheetsAPI
) {
    suspend fun getCategories(spreadsheetId : String, sheet : String): CategoriesResponse {
        return googleSheetsApi.getCategories(spreadsheetId, sheet)
    }

    suspend fun postExpense(expenseEntryRequest: ExpenseEntryRequest) : ExpenseEntryResponse {
        return googleSheetsApi.postExpense(expenseEntryRequest)
    }
}