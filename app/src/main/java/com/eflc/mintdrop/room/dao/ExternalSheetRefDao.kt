package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.eflc.mintdrop.room.dao.entity.ExternalSheetRef

@Dao
interface ExternalSheetRefDao {

    @Query("SELECT * FROM external_sheet_ref WHERE year = :year")
    fun getExternalSheetRefByYear(year: Int): ExternalSheetRef?
}
