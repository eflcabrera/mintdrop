package com.eflc.mintdrop.room

import androidx.room.TypeConverter
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.room.dao.entity.PaymentMethodType
import com.eflc.mintdrop.room.dao.entity.SharedExpenseConfigType
import com.eflc.mintdrop.room.dao.entity.TransferOperationType
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

    @TypeConverter
    fun stringToSharedExpenseConfigType(type: String?): SharedExpenseConfigType? {
        return type?.let { SharedExpenseConfigType.valueOf(it) }
    }

    @TypeConverter
    fun sharedExpenseConfigTypeToString(type: SharedExpenseConfigType?): String? {
        return type?.name
    }

    @TypeConverter
    fun stringToTransferOperationType(type: String?): TransferOperationType? {
        return type?.let { TransferOperationType.valueOf(it) }
    }

    @TypeConverter
    fun transferOperationTypeToString(type: TransferOperationType?): String? {
        return type?.name
    }
}
