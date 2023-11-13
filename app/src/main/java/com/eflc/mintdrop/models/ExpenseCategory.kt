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
data class ExpenseCategory(
    @Json(name = "name")
    val name: String,
    @Json(name = "subcategories")
    val subCategories: List<ExpenseSubCategory>
) : Parcelable {
    companion object NavigationType : NavType<ExpenseCategory>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): ExpenseCategory? {
            return bundle.getParcelable(key)
        }

        override fun parseValue(value: String): ExpenseCategory {
            return Gson().fromJson(value, ExpenseCategory::class.java)
        }

        override fun put(bundle: Bundle, key: String, value: ExpenseCategory) {
            bundle.putParcelable(key, value)
        }
    }
}
