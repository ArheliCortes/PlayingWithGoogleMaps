package com.arhelicortes.playingwithgooglemaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.SupportMapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var googleMap: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        googleMap = supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        googleMap.getMapAsync {

        }
    }
}