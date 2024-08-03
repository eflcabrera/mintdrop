package com.eflc.mintdrop.room.dao.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.eflc.mintdrop.room.dao.entity.Subcategory
import com.eflc.mintdrop.room.dao.entity.SubcategoryRow

data class SubcategoryAndSubcategoryRow(
    @Embedded val subcategory: Subcategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "subcategory_id"
    )
    val subcategoryRow: SubcategoryRow
)