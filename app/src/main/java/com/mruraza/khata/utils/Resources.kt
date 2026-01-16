package com.mruraza.khata.utils

sealed class Resources<out T> {
    object Loading : Resources<Nothing>()
    data class Success<T>(val data:T): Resources<T>()
    data class Error(val throwable: Throwable): Resources<Nothing>()
}