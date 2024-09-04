package com.eflc.mintdrop.room

import androidx.room.TypeConverter
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.room.dao.entity.PaymentMethodType
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

    @TypeConverter
    fun stringToPaymentMethodType(type: String?): PaymentMethodType? {
        return type?.let { PaymentMethodType.valueOf(it) }
    }

    @TypeConverter
    fun paymentMethodTypeToString(entryType: PaymentMethodType?): String? {
        return entryType?.name
    }
}
