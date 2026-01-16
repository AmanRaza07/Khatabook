package com.mruraza.khata.data.mapper

import com.mruraza.khata.Domain.model.Customer
import com.mruraza.khata.data.local.Entities.CustomerEntity

object customerMapper {
    fun CustomerEntity.toModel(): Customer =
        Customer(
            id = id,
            name = name,
            phone = phone,
            address = address,
            totalBalance = totalBalance
        )

    fun Customer.toEntity(): CustomerEntity =
        CustomerEntity(
            id = id,
            name = name,
            phone = phone,
            address = address,
            totalBalance = totalBalance
        )
}