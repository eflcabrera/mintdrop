package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.ExternalSheetRef

interface ExternalSheetRefRepository {
    suspend fun findExternalSheetRefByYear(year: Int): ExternalSheetRef?
}
