package com.eflc.mintdrop.service.shared

import com.eflc.mintdrop.utils.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class SettlementAdjustmentCalculatorTest {

    private val date = LocalDateTime.of(2024, 3, 15, 10, 0)

    @Test
    fun `carga positiva cuando pago el otro`() {
        val pending = listOf(
            SharedExpenseForSettlement(
                entryId = 1L,
                subcategoryId = 50L,
                amount = 100.0,
                description = "Supermercado",
                date = date,
                paidBy = Constants.THEIR_USER_ID,
                mySplit = 70.0
            )
        )

        val result = SettlementAdjustmentCalculator.computeSettlementAdjustments(pending)

        assertEquals(1, result.size)
        assertEquals(70.0, result[0].amount, 0.0)
        assertEquals(50L, result[0].subcategoryId)
        assertEquals("SETTLE Supermercado", result[0].description)
        assertEquals(date, result[0].date)
    }

    @Test
    fun `compensacion negativa cuando pague de mas`() {
        val pending = listOf(
            SharedExpenseForSettlement(
                entryId = 2L,
                subcategoryId = 60L,
                amount = 100.0,
                description = "Nafta",
                date = date,
                paidBy = Constants.MY_USER_ID,
                mySplit = 60.0
            )
        )

        val result = SettlementAdjustmentCalculator.computeSettlementAdjustments(pending)

        assertEquals(1, result.size)
        assertEquals(-40.0, result[0].amount, 0.0)
        assertEquals(60L, result[0].subcategoryId)
    }

    @Test
    fun `ajuste cero se omite`() {
        val pending = listOf(
            SharedExpenseForSettlement(
                entryId = 3L,
                subcategoryId = 70L,
                amount = 100.0,
                description = "Cine",
                date = date,
                paidBy = Constants.MY_USER_ID,
                mySplit = 100.0
            )
        )

        val result = SettlementAdjustmentCalculator.computeSettlementAdjustments(pending)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `redondeo de miParte a dos decimales sin residuo`() {
        val pending = listOf(
            SharedExpenseForSettlement(
                entryId = 4L,
                subcategoryId = 80L,
                amount = 100.0,
                description = "Split",
                date = date,
                paidBy = Constants.THEIR_USER_ID,
                mySplit = 33.333333
            )
        )

        val result = SettlementAdjustmentCalculator.computeSettlementAdjustments(pending)

        assertEquals(1, result.size)
        assertEquals(33.33, result[0].amount, 0.0)
    }

    @Test
    fun `fallback Otros para carga positiva con subcategoria invalida`() {
        val pending = listOf(
            SharedExpenseForSettlement(
                entryId = 5L,
                subcategoryId = 999L,
                amount = 100.0,
                description = "Borrada",
                date = date,
                paidBy = Constants.THEIR_USER_ID,
                mySplit = 50.0
            )
        )

        val result = SettlementAdjustmentCalculator.computeSettlementAdjustments(
            pending,
            resolveSubcategoryId = { _, amount ->
                SettlementAdjustmentCalculator.fallbackSubcategoryId(amount)
            }
        )

        assertEquals(1, result.size)
        assertEquals(Constants.DEFAULT_SETTLE_DEBIT_SUBCAT, result[0].subcategoryId)
        assertEquals(50.0, result[0].amount, 0.0)
    }

    @Test
    fun `fallback Reembolsos para credito con subcategoria invalida`() {
        val pending = listOf(
            SharedExpenseForSettlement(
                entryId = 6L,
                subcategoryId = 999L,
                amount = 100.0,
                description = "Borrada",
                date = date,
                paidBy = Constants.MY_USER_ID,
                mySplit = 40.0
            )
        )

        val result = SettlementAdjustmentCalculator.computeSettlementAdjustments(
            pending,
            resolveSubcategoryId = { _, amount ->
                SettlementAdjustmentCalculator.fallbackSubcategoryId(amount)
            }
        )

        assertEquals(1, result.size)
        assertEquals(Constants.DEFAULT_SETTLE_CREDIT_SUBCAT, result[0].subcategoryId)
        assertEquals(-60.0, result[0].amount, 0.0)
    }

    @Test
    fun `suma de ajustes equivale a owed menos paid`() {
        // paid=150, owed=180 → Σ ajustes = owed - paid = 30
        val pending = listOf(
            SharedExpenseForSettlement(
                entryId = 1L,
                subcategoryId = 10L,
                amount = 100.0,
                description = "A",
                date = date,
                paidBy = Constants.THEIR_USER_ID,
                mySplit = 70.0
            ),
            SharedExpenseForSettlement(
                entryId = 2L,
                subcategoryId = 20L,
                amount = 100.0,
                description = "B",
                date = date,
                paidBy = Constants.MY_USER_ID,
                mySplit = 60.0
            ),
            SharedExpenseForSettlement(
                entryId = 3L,
                subcategoryId = 30L,
                amount = 50.0,
                description = "C",
                date = date,
                paidBy = Constants.MY_USER_ID,
                mySplit = 50.0
            )
        )

        val result = SettlementAdjustmentCalculator.computeSettlementAdjustments(pending)
        assertEquals(30.0, result.sumOf { it.amount }, 0.0)
    }
}
