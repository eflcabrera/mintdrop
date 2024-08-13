package com.eflc.mintdrop.room.dao.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.eflc.mintdrop.room.dao.entity.Category
import com.eflc.mintdrop.room.dao.entity.Subcategory

data class CategoryAndSubcategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "uid",
        entityColumn = "category_id",
        entity = Subcategory::class
    )
    val subcategories: List<SubcategoryAndSubcategoryRow>
)
