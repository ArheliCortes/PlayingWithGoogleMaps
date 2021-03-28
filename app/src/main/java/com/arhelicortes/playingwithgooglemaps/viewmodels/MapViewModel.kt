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

    // TODO : Crear LiveData de isTrafficEnabled
    private val _isTrafficEnabled = MutableLiveData(false)
    val isTrafficEnabled: LiveData<Boolean> = _isTrafficEnabled

    private val _mapStyle = MutableLiveData<Int?>()
    val mapStyle: LiveData<Int?> = _mapStyle

    private val _currentMarker = MutableLiveData<Marker?>(null)
    val currentMarker: LiveData<Marker?> = _currentMarker

    private val _circleVisibility = MutableLiveData(false)
    val circleVisibility: MutableLiveData<Boolean> = _circleVisibility

    private val _polygonVisibility = MutableLiveData(false)
    val polygonVisibility: MutableLiveData<Boolean> = _polygonVisibility

    private val _polylineVisibility = MutableLiveData(false)
    val polylineVisibility: MutableLiveData<Boolean> = _polylineVisibility


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
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)

    }

    fun onTrafficChange(isTrafficEnabled: Boolean) {
        _isTrafficEnabled.value = isTrafficEnabled
    }

    fun onStyleSelect(style: Int?) {
        _mapStyle.value = style
    }

    fun switchMarker(marker: Marker) {
        _currentMarker.value?.remove()
        _currentMarker.value = marker
    }

    fun changeCircleVisibility(isVisible: Boolean) {
        _circleVisibility.value = isVisible
    }

    fun changePolygonVisibility(isVisible: Boolean) {
        _polygonVisibility.value = isVisible
    }

    fun changePolylineVisibility(isVisible: Boolean) {
        _polylineVisibility.value = isVisible
    }
}