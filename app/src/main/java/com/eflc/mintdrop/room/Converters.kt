package com.eflc.mintdrop.room

import androidx.room.TypeConverter
import com.eflc.mintdrop.models.EntryType
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun stringToEntryType(type: String?): EntryType? {
        return type?.let { EntryType.valueOf(it) }
    }

    @TypeConverter
    fun entryTypeToString(entryType: EntryType?): String? {
        return entryType?.name
    }
}