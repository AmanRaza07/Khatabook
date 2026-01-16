package com.mruraza.khata.data.mapper

import com.mruraza.khata.Domain.model.Transaction
import com.mruraza.khata.data.converter.AppTypeConverters
import com.mruraza.khata.data.local.Entities.TransactionEntity

object TransactionMapper {
    fun TransactionEntity.toModel(): Transaction =
        Transaction(
            id = id,
            customerId = customerId,
            due = due,
            paid = paid,
            discount = discount,
            items = AppTypeConverters().toItemsList(items),
            note = note,
            timestamp = timestamp
        )

    fun Transaction.toEntity(): TransactionEntity =
        TransactionEntity(
            id = id,
            customerId = customerId,
            due = due,
            paid = paid,
            discount = discount,
            items = AppTypeConverters().fromItemsList(items),
            note = note,
            timestamp = timestamp
        )
}