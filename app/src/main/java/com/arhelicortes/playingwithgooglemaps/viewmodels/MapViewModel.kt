package com.arhelicortes.playingwithgooglemaps.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.Marker

class MapViewModel(val app: Application) : AndroidViewModel(app) {

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    val isTrafficEnabled = MutableLiveData(true)
    private val _mapStyle = MutableLiveData<Int?>()
    val mapStyle: LiveData<Int?> = _mapStyle

    private val _currentMarker = MutableLiveData<Marker?>(null)
    val currentMarker: LiveData<Marker?> = _currentMarker


    val circleVisibility= MutableLiveData(false)


    val polygonVisibility= MutableLiveData(true)

    val polylineVisibility= MutableLiveData(true)


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            _location.value = locationResult.lastLocation
        }

    }


    fun onMapLoaded() {
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(app)

        if (checkPermission()) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            _location.value = location
        }
        val locationRequest = LocationRequest.create().apply {
            smallestDisplacement = 5f
            interval = 1000L

        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)

    }

    fun onStyleSelect(style: Int?) {
        _mapStyle.value = style
    }

    fun switchMarker(marker: Marker) {
        _currentMarker.value?.remove()
        _currentMarker.value = marker
    }

}