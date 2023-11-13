package com.eflc.mintdrop.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleSheetsResponse(
    @Json(name = "categories")
    val categories: List<ExpenseCategory>
)
