package com.carlyadam.routes.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.carlyadam.routes.R
import com.carlyadam.routes.databinding.ActivityMapsBinding
import com.carlyadam.routes.utils.MarkerAnimation.animateMarkerToICS
import com.carlyadam.routes.utils.MapsUtils.calculateBearing
import com.carlyadam.routes.utils.MapsUtils.changeMarkerSize
import com.carlyadam.routes.utils.MapsUtils.decodePolyline
import com.carlyadam.routes.utils.MapsUtils.rotateMarker
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.RuntimeExecutionException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var oldLat: Double = 0.0
    private var oldLng: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var myMarker: Marker? = null
    private var myMarkerPosition: LatLng? = null
    private var bearing: Float? = null
    private val mapsViewModel: MapsViewModel by viewModels()
    private var lines: String = ""
    private var polyline: Polyline? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsViewModel.responseLiveData.observe(this, Observer {
            if (polyline != null) {
                polyline!!.remove()
            }
            if (!it.routes.isEmpty()) {
                lines = it.routes[0].overview_polyline.points
                polyline = mMap!!.addPolyline(
                    PolylineOptions().addAll(decodePolyline(lines)).width(7F).color(Color.GRAY)
                )
            } else {
                showToast(getString(R.string.ruta_error))
            }
        })

        mapsViewModel.errorLiveData.observe(this, Observer {
            showToast(it)
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requestPermissions()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

    private fun routes(
        origin: String
    ) {
        lifecycleScope.launch {
            mapsViewModel.routes(
                origin
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap!!.isMyLocationEnabled = false

        bearing =
            calculateBearing(
                oldLat, oldLng,
                lat, lng
            )

        myMarkerPosition = LatLng(lat, lng)

        myMarker = mMap.addMarker(
            MarkerOptions()
                .position(myMarkerPosition!!)
                .rotation(bearing!!)
                .icon(BitmapDescriptorFactory.fromBitmap(changeMarkerSize(R.drawable.marker, this)))
                .flat(true)
                .title("Position")
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myMarkerPosition))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(19.0f))
    }

    private fun getLastLocation() {
        if (checkPermissions() && isLocationEnabled()) {
            try {
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    try {
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            updateMyPosition(location.latitude, location.longitude)
                            requestNewLocationData()
                        }


                    } catch (r: RuntimeExecutionException) {
                        r.printStackTrace()
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            showToast(getString(R.string.no_location_error))
        }
    }

    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.smallestDisplacement = 10F
        try {
            fusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            updateMyPosition(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    private fun updateMyPosition(latitude: Double, longitude: Double) {
        if (myMarker != null) {
            oldLat = lat
            oldLng = lng
            lat = latitude
            lng = longitude

            bearing =
                calculateBearing(
                    oldLat,
                    oldLng,
                    lat,
                    lng
                )

            myMarkerPosition = LatLng(lat, lng)

            rotateMarker(myMarker!!, bearing!!)
            animateMarkerToICS(myMarker, myMarkerPosition)

            val cameraPosition = CameraPosition.Builder()
                .target(myMarkerPosition)
                .zoom(19.0f)
                .build()
            val cu = CameraUpdateFactory.newCameraPosition(cameraPosition)
            mMap.animateCamera(cu)

            routes("$lat,$lng")

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback)
        }
    }
}