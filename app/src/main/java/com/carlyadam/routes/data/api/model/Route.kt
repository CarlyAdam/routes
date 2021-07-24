package com.carlyadam.routes.data.api.model

data class Route(
    val bounds:Any,
    val copyrights: String,
    val legs: Any,
    val overview_polyline: OverviewPolyline,
    val summary: String,
    val warnings: Any,
    val waypoint_order: Any
)