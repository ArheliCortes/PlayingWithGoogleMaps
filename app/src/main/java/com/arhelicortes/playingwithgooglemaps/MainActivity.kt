package com.arhelicortes.playingwithgooglemaps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arhelicortes.playingwithgooglemaps.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity() {


    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var googleMap: SupportMapFragment
    private lateinit var map: GoogleMap
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableLocation()
        googleMap = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment

        googleMap.getMapAsync {
            map = it
            mapViewModel.onMapLoaded()
        }

        setUpObservers()
    }

    private fun setUpObservers(){
        mapViewModel.location.observe(this, Observer{
            val latLng = LatLng(it.latitude,it.longitude)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLng,12f)
            )
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)&& (grantResults[1] == PackageManager.PERMISSION_GRANTED))
                enableLocation()
        }
    }

    private fun enableLocation(){
        if(!isPermissionGranted()){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun isPermissionGranted():Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}