package com.eflc.mintdrop.service.shared

import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail
import com.eflc.mintdrop.room.dao.entity.relationship.EntryRecordAndSharedExpenseDetails

interface SharedExpenseService {

    suspend fun createSharedExpenseEntries(sharedEntryRecord: EntryHistory, entryRecordId: Long): List<SharedExpenseEntryDetail>
    suspend fun deleteSharedExpenseEntries(sharedEntryRecord: EntryHistory)

    /**
     * Crea la cabecera [com.eflc.mintdrop.room.dao.entity.SharedExpenseSettlement]
     * y marca los gastos pendientes como saldados. No crea asientos de ajuste.
     * Debe ejecutarse dentro de la misma withTransaction que encola los ajustes.
     *
     * @param netBalance paid - owed del usuario actual (define CREDIT/DEBIT de la cabecera)
     * @return uid del settlement creado
     */
    suspend fun saveSettlementAndMarkSettled(
        pendingSharedExpenses: List<EntryRecordAndSharedExpenseDetails>,
        netBalance: Double
    ): Long
}
