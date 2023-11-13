package com.eflc.mintdrop.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExpenseEntryRequest(
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
    val description: String
)
