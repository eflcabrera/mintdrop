package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.ExternalSheetRefRepository
import com.eflc.mintdrop.room.dao.ExternalSheetRefDao
import com.eflc.mintdrop.room.dao.entity.ExternalSheetRef
import javax.inject.Inject

class ExternalSheetRefRepositoryImpl @Inject constructor(
    private val dao: ExternalSheetRefDao
) : ExternalSheetRefRepository {
    override suspend fun findExternalSheetRefByYear(year: Int): ExternalSheetRef? {
        return dao.getExternalSheetRefByYear(year)
    }
}
