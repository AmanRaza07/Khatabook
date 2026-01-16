package com.mruraza.khata.data.local.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ItemsEntity")
data class ItemsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val price: Double
)
