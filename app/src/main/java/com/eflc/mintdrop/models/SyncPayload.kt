package com.eflc.mintdrop.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncPayload(
    @Json(name = "entryHistoryId")
    val entryHistoryId: Long,
    @Json(name = "spreadsheet_id")
    val spreadsheetId: String,
    @Json(name = "sheet")
    val sheetName: String,
    @Json(name = "month")
    val month: Int,
    @Json(name = "row")
    val row: Int,
    @Json(name = "amount")
    val amount: Double,
    @Json(name = "description")
    val description: String,
    @Json(name = "isOwedInstallments")
    val isOwedInstallments: Boolean,
    @Json(name = "totalInstallments")
    val totalInstallments: Int,
    @Json(name = "paymentMethod")
    val paymentMethod: String
) {
    fun toExpenseEntryRequest(): ExpenseEntryRequest {
        return ExpenseEntryRequest(
            spreadsheetId = spreadsheetId,
            sheetName = sheetName,
            month = month,
            row = row,
            amount = amount,
            description = description,
            isOwedInstallments = isOwedInstallments,
            totalInstallments = totalInstallments,
            paymentMethod = paymentMethod
        )
    }
}


