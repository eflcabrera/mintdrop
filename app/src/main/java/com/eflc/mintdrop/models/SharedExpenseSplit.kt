package com.eflc.mintdrop.models

data class SharedExpenseSplit(
    val userId: Long,
    var owed: Double,
    var paid: Double
)
