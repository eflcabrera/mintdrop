package com.eflc.mintdrop.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryManagementRequest(
    @Json(name = "action")
    val action: String,
    @Json(name = "spreadsheetId")
    val spreadsheetId: String,
    @Json(name = "sheetName")
    val sheetName: String,
    @Json(name = "categoryId")
    val categoryId: String? = null,
    @Json(name = "categoryName")
    val categoryName: String? = null,
    @Json(name = "subcategoryId")
    val subcategoryId: String? = null,
    @Json(name = "subcategoryName")
    val subcategoryName: String? = null,
    @Json(name = "newName")
    val newName: String? = null
)

@JsonClass(generateAdapter = true)
data class CategoryManagementResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "message")
    val message: String,
    @Json(name = "rowNumber")
    val rowNumber: Int? = null
)




