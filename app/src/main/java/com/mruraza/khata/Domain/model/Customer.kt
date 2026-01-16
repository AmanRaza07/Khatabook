package com.mruraza.khata.Domain.model

data class Customer(
    val id: Int = 0,
    val name: String,
    val phone: String?,
    val address: String?,
    val totalBalance: Double
)

