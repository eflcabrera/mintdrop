package com.eflc.mintdrop.models

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExpenseCategory(
    override val id: String,
    override val name: String,
    @Json(name = "subcategories")
    val subCategories: List<ExpenseSubCategory>
) : Parcelable, GenericCategory {
    
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(ExpenseSubCategory.CREATOR)!!
    )
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeTypedList(subCategories)
    }
    
    override fun describeContents(): Int {
        return 0
    }
    
    companion object : NavType<ExpenseCategory>(isNullableAllowed = false) {
        @JvmField
        val CREATOR: Parcelable.Creator<ExpenseCategory> = object : Parcelable.Creator<ExpenseCategory> {
            override fun createFromParcel(parcel: Parcel): ExpenseCategory {
                return ExpenseCategory(parcel)
            }
            
            override fun newArray(size: Int): Array<ExpenseCategory?> {
                return arrayOfNulls(size)
            }
        }
        
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
