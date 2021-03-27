package com.arhelicortes.playingwithgooglemaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.map
import com.arhelicortes.playingwithgooglemaps.databinding.ActivityMainBinding
import com.arhelicortes.playingwithgooglemaps.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ktx.addCircle
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolygon
import com.google.maps.android.ktx.addPolyline

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var googleMap: SupportMapFragment
    private lateinit var map: GoogleMap
    private val mapViewModel: MapViewModel by viewModels()
    private var currentMarker: Marker? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableLocation()
        googleMap = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment

        googleMap.getMapAsync {
            map = it
            //map.isTrafficEnabled = true

            map.isMyLocationEnabled = true
            map.setOnPoiClickListener{
                createMarkerForPOI(it)
            }
            mapViewModel.onMapLoaded()
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapa))
            val configs: Configuration = resources.configuration
            when (configs.uiMode and Configuration.UI_MODE_NIGHT_MASK){
                Configuration.UI_MODE_NIGHT_YES->{
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapa_night))
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    map.setMapStyle(null)
                }
            }
            addPolyLine()
            addPolygon()
            addCircle()
            setUpViews()
        }

        setUpObservers()

    }

    private fun setUpViews() {
        binding.switchMaterial.setOnCheckedChangeListener{
            buttonView,isChecked ->
            map.isTrafficEnabled = isChecked
        }
        val list = listOf("Default","Día","Noche","Estilo A")
        val adapter = ArrayAdapter(this,R.layout.item_list,list)
        binding.actvMapas.setAdapter(adapter)
        binding.actvMapas.setOnItemClickListener{parent,view,position,id ->
            when (position){
                0 -> map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapa))
                1 -> map.setMapStyle(null)
                2 -> map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapa_night))
                3 -> map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapa_arheli))

            }
        }
    }


    private fun createMarkerForPOI(it:PointOfInterest?){
        currentMarker?.remove()

        val marker = map.addMarker {
            title(it?.name)
            position(it?.latLng!!)
        }
        currentMarker = marker
    }

    private fun setUpObservers() {
        mapViewModel.location.observe(this, Observer {
            if(!::map.isInitialized){
                return@Observer
            }
            val latLng = LatLng(it.latitude, it.longitude)
            map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, 12f)
            )
            currentMarker?.remove()
            val marker = map.addMarker{
                title("¡Estas aquí!")
                snippet("Aquí es donde estas")
                position(latLng)
            }
            marker.showInfoWindow()
            currentMarker = marker

        })
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))
                enableLocation()
        }
    }

    private fun enableLocation() {
        if (!isPermissionGranted()) {
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

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun addCircle() {
        map.addCircle{
            center(LatLng(19.43260902888549, -99.1323094033432))
            fillColor(Color.RED)
            strokeColor(Color.BLACK)
            radius(4520.0)
        }
    }

    private fun addPolygon() {
        val list = listOf(
                LatLng(19.435149368650233, -99.1462951547011),
                LatLng(19.437081109756843, -99.14590173848903),
                LatLng(19.436223982433265, -99.14173695307166),
                LatLng(19.43442016239011, -99.14213036928373)
        )
        map.addPolygon{
            addAll(list)
            fillColor(Color.BLACK)
            strokeColor(Color.BLUE)
        }
    }

    private fun addPolyLine() {
        val list = listOf(
                LatLng(19.44380043947042, -99.13887110802582),
                LatLng(19.435914996110313, -99.14171072694656),
                LatLng(19.43137161467975, -99.14157564653115),
                LatLng(19.42716781224051, -99.1419358609266),
                LatLng(19.421562572826605, -99.1430615309206),
                LatLng(19.413111884746243, -99.14382698651092),
                LatLng(19.409119949672196, -99.1355870822152),
                LatLng(19.406199941081958, -99.1264035113321),
                LatLng(19.404076493310736, -99.12086521500218),
                LatLng(19.39817316267726, -99.11325568581947),
                LatLng(19.38865943101581, -99.1120399622218),
                LatLng(19.378653284063983, -99.10920744470712),
                LatLng(19.37305866770402, -99.10769033873527),
                LatLng(19.364644696612373, -99.10920744469585),
                LatLng(19.356136292019528, -99.10100776631812),
                LatLng(19.357479596408936, -99.09322854477833),
                LatLng(19.355871890562817, -99.08561721725812),
                LatLng(19.350941493821505, -99.07482503644592),
                LatLng(19.346064541764708, -99.06414645753699)
        )
        map.addPolyline{
            color(Color.GREEN)
            addAll(list)
        }
    }
}