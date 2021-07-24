package com.carlyadam.routes.data.api

import java.io.IOException

suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> =
    try {
        call.invoke()
    } catch (e: Exception) {
        e.printStackTrace()
        Result.Error(IOException(errorMessage, e))
    }