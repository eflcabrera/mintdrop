package com.eflc.mintdrop.room.dao.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.Subcategory

data class CategoryAndSubcategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "category_id"
    )
    val subcategories: List<Subcategory>
)
