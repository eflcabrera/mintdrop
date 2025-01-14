package com.eflc.mintdrop.room.dao.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.SharedExpenseEntryDetail

data class EntryRecordAndSharedExpenseDetails (
    @Embedded val entryRecord: EntryHistory,
    @Relation(
        parentColumn = "uid",
        entityColumn = "entry_record_id",
        entity = SharedExpenseEntryDetail::class
    )
    val sharedExpenseDetails: List<SharedExpenseEntryDetail>
)
