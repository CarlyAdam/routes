package com.carlyadam.routes.repository

import android.content.Context
import com.carlyadam.routes.R
import com.carlyadam.routes.data.api.ApiService
import com.carlyadam.routes.data.api.ApiService.Companion.DESTINATION
import com.carlyadam.routes.data.api.Result
import com.carlyadam.routes.data.api.responses.GoogleApiResponse
import com.carlyadam.routes.data.api.safeApiCall
import java.io.IOException

class MapsRepository(
    private val apiService: ApiService,
    private val context: Context
) {

    suspend fun routes(
        origin: String
    ) =
        safeApiCall(
            call = {
                getRoutes(origin)
            },
            errorMessage = context.getString(R.string.no_connection)
        )

    private suspend fun getRoutes(
        origin: String
    ): Result<GoogleApiResponse> {

        val response = apiService.mapAddress(
            origin,
            DESTINATION,
            context.getString(R.string.google_maps_key)
        )
        if (response.isSuccessful) {
            return Result.Success(response.body()!!)
        }
        return Result.Error(IOException(context.getString(R.string.no_connection)))
    }
}