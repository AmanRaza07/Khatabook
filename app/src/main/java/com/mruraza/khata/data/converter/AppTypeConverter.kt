package com.mruraza.khata.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mruraza.khata.Domain.model.TransactionItem

class AppTypeConverters {

    @TypeConverter
    fun fromItemsList(list: List<TransactionItem>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toItemsList(json: String): List<TransactionItem> {
        val type = object : TypeToken<List<TransactionItem>>() {}.type
        return Gson().fromJson(json, type)
    }
}