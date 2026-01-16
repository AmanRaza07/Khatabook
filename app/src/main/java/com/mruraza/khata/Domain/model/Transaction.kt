package com.mruraza.khata.Domain.model

data class Transaction(
    val id: Int = 0,
    val customerId: Int,
    val due: Double,
    val paid: Double,
    val discount: Double,
    val items: List<TransactionItem>,
    val note: String? = null,
    val timestamp: Long
)

data class TransactionItem(
    val name: String,
    val price: Double,
    val quantity: Double
)

