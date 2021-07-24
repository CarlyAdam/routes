package com.carlyadam.routes.data.api

import com.carlyadam.routes.data.api.responses.GoogleApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    companion object {
        const val API_URL = "https://maps.googleapis.com/maps/api/directions/"
        const val DESTINATION = "LAT, LNG"
    }

    @GET("json")
    suspend fun mapAddress(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("key") key: String?
    ): Response<GoogleApiResponse>
}