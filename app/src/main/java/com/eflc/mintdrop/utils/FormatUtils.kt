package com.eflc.mintdrop.utils

import java.text.NumberFormat
import java.util.Locale

class FormatUtils {
    companion object {
        fun formatAsCurrency(amount: Double): String {
            val numberFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.GERMAN)
            numberFormat.maximumFractionDigits = 2
            return numberFormat.format(amount)
        }
    }
}
