package com.eflc.mintdrop.models

import androidx.compose.ui.graphics.Color

data class SubCategoryBalanceData(
    val subcategoryId: Long,
    val subcategoryName: String,
    val balance: Double,
    val month: Int,
    val year: Int,
    val color: Color? = null
)
