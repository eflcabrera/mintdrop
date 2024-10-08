package com.eflc.mintdrop.repository.impl

import com.eflc.mintdrop.repository.PaymentMethodRepository
import com.eflc.mintdrop.room.dao.PaymentMethodDao
import com.eflc.mintdrop.room.dao.entity.PaymentMethod
import javax.inject.Inject

class PaymentMethodRepositoryImpl @Inject constructor(
    private val dao: PaymentMethodDao
) : PaymentMethodRepository {
    override suspend fun findAllPaymentMethods(): List<PaymentMethod> {
        return dao.getPaymentMethods()
    }
}
