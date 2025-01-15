package com.eflc.mintdrop.room.dao.entity.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.eflc.mintdrop.room.dao.entity.SharedExpenseConfiguration
import com.eflc.mintdrop.room.dao.entity.SharedExpenseConfigurationDetail

data class SharedExpenseConfigurationAndDetails (
    @Embedded
    val configuration: SharedExpenseConfiguration,
    @Relation(
        parentColumn = "uid",
        entityColumn = "configuration_id",
        entity = SharedExpenseConfigurationDetail::class
    )
    val details: List<SharedExpenseConfigurationDetail>
)
