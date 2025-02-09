package com.eflc.mintdrop.models

data class SharedExpenseBalanceData(
    var total: Double,
    var splits: List<SharedExpenseSplit>
)
