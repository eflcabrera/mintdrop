package com.eflc.mintdrop.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class FormatUtils {
    companion object {
        fun formatAsCurrency(amount: Double): String {
            val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
            numberFormat.maximumFractionDigits = 2
            return numberFormat.format(amount)
        }

        fun convertMillisToStringDate(millis: Long): String {
            val zonedDateTime = Instant
                .ofEpochMilli(millis)
                .atZone(ZoneOffset.systemDefault())
            val instant = zonedDateTime.toInstant()
            val dateFormat = "dd/MM/yyyy"
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            return formatter.format(Date.from(instant))
        }

        fun formatDateFromString(dateString: String, pattern: String? = "dd/MM/yyyy"): LocalDate {
            val dateFormatter = DateTimeFormatter.ofPattern(pattern)
            return LocalDate.parse(dateString, dateFormatter)
        }
    }
}
