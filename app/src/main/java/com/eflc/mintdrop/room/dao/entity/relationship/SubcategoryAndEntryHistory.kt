package com.eflc.mintdrop.room.dao.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.eflc.mintdrop.room.dao.entity.EntryHistory
import com.eflc.mintdrop.room.dao.entity.Subcategory

data class SubcategoryAndEntryHistory(
    @Embedded val subcategory: Subcategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "subcategory_id"
    )
    val subcategories: List<EntryHistory>
)
