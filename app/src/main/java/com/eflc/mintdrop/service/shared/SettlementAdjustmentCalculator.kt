package com.eflc.mintdrop.service.shared

import com.eflc.mintdrop.utils.Constants
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

data class SharedExpenseForSettlement(
    val entryId: Long,
    val subcategoryId: Long,
    val amount: Double,
    val description: String,
    val date: LocalDateTime,
    val paidBy: Long?,
    val mySplit: Double
)

data class SettlementAdjustment(
    val sharedEntryId: Long,
    val subcategoryId: Long,
    val date: LocalDateTime,
    val amount: Double,
    val description: String
)

/**
 * Cálculo puro de ajustes de liquidación distribuida
 * Sin I/O: la validez de subcategoría / fallback se aplica vía [resolveSubcategoryId].
 */
object SettlementAdjustmentCalculator {

    fun computeSettlementAdjustments(
        pending: List<SharedExpenseForSettlement>,
        currentUserId: Long = Constants.MY_USER_ID,
        resolveSubcategoryId: (originalSubcategoryId: Long, amount: Double) -> Long = { original, _ -> original }
    ): List<SettlementAdjustment> {
        return pending.mapNotNull { expense ->
            val myPart = roundToTwoDecimals(expense.mySplit)
            val alreadyPosted = if (expense.paidBy == currentUserId) expense.amount else 0.0
            val adjustment = roundToTwoDecimals(myPart - alreadyPosted)
            if (adjustment == 0.0) {
                null
            } else {
                SettlementAdjustment(
                    sharedEntryId = expense.entryId,
                    subcategoryId = resolveSubcategoryId(expense.subcategoryId, adjustment),
                    date = expense.date,
                    amount = adjustment,
                    description = Constants.SETTLE_DESCRIPTION_PREFIX + expense.description
                )
            }
        }
    }

    fun roundToTwoDecimals(value: Double): Double {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun fallbackSubcategoryId(amount: Double): Long {
        return if (amount > 0.0) {
            Constants.DEFAULT_SETTLE_DEBIT_SUBCAT
        } else {
            Constants.DEFAULT_SETTLE_CREDIT_SUBCAT
        }
    }
}
