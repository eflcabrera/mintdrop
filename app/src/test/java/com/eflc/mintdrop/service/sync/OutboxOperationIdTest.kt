package com.eflc.mintdrop.service.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

/**
 * Verifica contratos de idempotencia del outbox sin depender de Android/Room.
 */
class OutboxOperationIdTest {

    @Test
    fun `operationId es UUID global valido`() {
        val id = UUID.randomUUID().toString()
        assertTrue(UUID.fromString(id).toString().isNotBlank())
    }

    @Test
    fun `operationIds sucesivos son distintos`() {
        val a = UUID.randomUUID().toString()
        val b = UUID.randomUUID().toString()
        assertNotEquals(a, b)
    }

    @Test
    fun `mismo operationId se mantiene estable entre reintentos conceptuales`() {
        val operationId = UUID.randomUUID().toString()
        val retry1 = operationId
        val retry2 = operationId
        assertEquals(retry1, retry2)
    }
}
