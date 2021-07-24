package com.carlyadam.routes.data.api.responses

import com.carlyadam.routes.data.api.model.Route

data class GoogleApiResponse(
    val geocoded_waypoints: Any,
    val routes: List<Route>,
    val status: String
)