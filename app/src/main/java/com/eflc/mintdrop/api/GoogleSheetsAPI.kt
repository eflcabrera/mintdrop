package com.eflc.mintdrop.api

import com.eflc.mintdrop.models.ExpenseEntryRequest
import com.eflc.mintdrop.models.GoogleSheetsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleSheetsAPI {
    @GET("exec")
    suspend fun getCategories(@Query("spreadsheetId") spreadsheetId : String, @Query("sheet") sheet : String) : GoogleSheetsResponse

    @POST("exec")
    suspend fun postExpense(@Body body: ExpenseEntryRequest)
}