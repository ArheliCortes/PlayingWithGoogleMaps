package com.arhelicortes.playingwithgooglemaps.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapViewModel(val app: Application) : AndroidViewModel(app){

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    fun onMapLoaded(){
        getDeviceLocation()
    }

    private fun getDeviceLocation(){
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(app)

        if(checkPermission()){
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            _location.value = location
        }
    }

    private fun checkPermission():Boolean{
        return ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
    }
}