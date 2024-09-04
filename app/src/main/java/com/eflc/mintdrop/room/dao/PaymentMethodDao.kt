package com.eflc.mintdrop.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.eflc.mintdrop.room.dao.entity.PaymentMethod

@Dao
interface PaymentMethodDao {

    @Query("SELECT * FROM payment_method ORDER BY uid ASC")
    fun getPaymentMethods(): List<PaymentMethod>
}
