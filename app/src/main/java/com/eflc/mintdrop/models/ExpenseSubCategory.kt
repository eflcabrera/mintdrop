package com.eflc.mintdrop.models

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExpenseSubCategory(
    override val id: String,
    override val name: String,
    @Json(name = "rowNumber")
    val rowNumber: Int
) : Parcelable, GenericCategory {
    
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    )
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(rowNumber)
    }
    
    override fun describeContents(): Int {
        return 0
    }
    
    companion object : NavType<ExpenseSubCategory>(isNullableAllowed = false) {
        @JvmField
        val CREATOR: Parcelable.Creator<ExpenseSubCategory> = object : Parcelable.Creator<ExpenseSubCategory> {
            override fun createFromParcel(parcel: Parcel): ExpenseSubCategory {
                return ExpenseSubCategory(parcel)
            }
            
            override fun newArray(size: Int): Array<ExpenseSubCategory?> {
                return arrayOfNulls(size)
            }
        }
        
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
