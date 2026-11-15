package com.eflc.mintdrop.models

/**
 * Resultado de saldar gastos compartidos
 */
sealed class SettleSharedExpensesResult {
    data object Success : SettleSharedExpensesResult()
    data class Failure(val message: String) : SettleSharedExpensesResult()
}

/**
 * Estado agregado de sync de asientos SETTLE pendientes en la pantalla de compartidos.
 */
enum class SettleSyncAggregateState {
    /** No hay asientos SETTLE pendientes de sync. */
    NONE,
    /** Hay al menos una tarea PENDING o IN_PROGRESS. */
    IN_FLIGHT,
    /** Hay asientos unsynced y todas las tareas activas restantes están en FAILED. */
    ALL_FAILED
}
