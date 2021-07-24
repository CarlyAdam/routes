package com.carlyadam.routes.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.SystemClock
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil

object MapsUtils {

    fun calculateBearing(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val sourceLatLng = LatLng(lat1, lng1)
        val destinationLatLng = LatLng(lat2, lng2)
        return SphericalUtil.computeHeading(sourceLatLng, destinationLatLng).toFloat()
    }

    fun changeMarkerSize(drawable: Int, context: Context): Bitmap {
        val height = 140
        val width = 110
        val bitmapdraw =
            ContextCompat.getDrawable(context, drawable) as BitmapDrawable
        val b = bitmapdraw.bitmap
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    fun rotateMarker(marker: Marker, mBearing: Float) {
        var isMarkerRotating = false
        if (!isMarkerRotating) {
            val handler = Handler()
            val start: Long = SystemClock.uptimeMillis()
            val startRotation = marker.rotation
            val duration: Long = 2000 // Change duration as you want
            val interpolator: Interpolator = LinearInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    isMarkerRotating = true
                    val elapsed: Long = SystemClock.uptimeMillis() - start
                    val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    val rot = t * mBearing + (1 - t) * startRotation
                    val bearing = if (-rot > 180) rot / 2 else rot
                    marker.rotation = bearing
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 4)
                    } else {
                        isMarkerRotating = false
                    }
                }
            })
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        return PolyUtil.decode(encoded)
    }

}