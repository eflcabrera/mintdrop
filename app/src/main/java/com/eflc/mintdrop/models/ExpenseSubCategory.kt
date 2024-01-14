package com.eflc.mintdrop.models

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ExpenseSubCategory(
    override val name: String,
    @Json(name = "rowNumber")
    val rowNumber: Int
) : Parcelable, GenericCategory {
    companion object NavigationType : NavType<ExpenseSubCategory>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): ExpenseSubCategory? {
            return bundle.getParcelable(key)
        }

        override fun parseValue(value: String): ExpenseSubCategory {
            return Gson().fromJson(value, ExpenseSubCategory::class.java)
        }

        override fun put(bundle: Bundle, key: String, value: ExpenseSubCategory) {
            bundle.putParcelable(key, value)
        }
    }
}
