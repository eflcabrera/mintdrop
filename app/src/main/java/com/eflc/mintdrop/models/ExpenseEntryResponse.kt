package com.eflc.mintdrop.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExpenseEntryResponse(
    @Json(name = "sheet")
    val sheet: String,
    @Json(name = "previousAmount")
    val previousAmount: Double,
    @Json(name = "finalAmount")
    val finalAmount: Double
)
