package com.carlyadam.routes.data.api

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class UnAuthorized(val message: String) : Result<Nothing>()
}