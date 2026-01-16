package com.mruraza.khata.utils

import android.content.Context

fun getOwnerProfile(context: Context): OwnerProfile {
    val sharedPref = context.getSharedPreferences("owner_prefs", Context.MODE_PRIVATE)
    val name = sharedPref.getString("owner_name", "") ?: ""
    val address = sharedPref.getString("owner_address", "") ?: ""
    val phone = sharedPref.getString("owner_phone", "") ?: ""
    return OwnerProfile(name, address, phone)
}

data class OwnerProfile(
    val name: String,
    val address: String,
    val phone: String
)
