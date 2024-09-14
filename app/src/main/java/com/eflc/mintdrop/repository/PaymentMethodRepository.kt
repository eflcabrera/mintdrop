package com.eflc.mintdrop.repository

import com.eflc.mintdrop.room.dao.entity.PaymentMethod

interface PaymentMethodRepository {
    suspend fun findAllPaymentMethods(): List<PaymentMethod>
}
