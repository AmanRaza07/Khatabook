package com.mruraza.khata.data.mapper

import com.mruraza.khata.Domain.model.Items
import com.mruraza.khata.data.local.Entities.ItemsEntity

object ItemsMapper {
    fun Items.toEntity(): ItemsEntity =
        ItemsEntity(
            id = id,
            name = name,
            price = price
        )
    fun ItemsEntity.toModel(): Items =
        Items(
            id = id,
            name = name,
            price = price
        )
}


