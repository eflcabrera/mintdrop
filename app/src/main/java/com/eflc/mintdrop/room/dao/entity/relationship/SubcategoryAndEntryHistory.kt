package com.eflc.mintdrop.room.dao.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.Subcategory

data class SubcategoryAndEntryHistory(
    @Embedded val subcategory: Subcategory,
    @Relation(
        parentColumn = "uid",
        entityColumn = "subcategory_id",
        entity = EntryHistory::class
    )
    val entries: List<EntryHistory>
)
