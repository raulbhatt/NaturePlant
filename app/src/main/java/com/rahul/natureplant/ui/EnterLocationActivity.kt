package com.rahul.natureplant.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.rahul.natureplant.MainActivity
import com.rahul.natureplant.adapter.LocationAdapter
import com.rahul.natureplant.model.Location
import com.rahul.natureplant.R
import java.io.IOException
import java.util.Locale

class EnterLocationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: LatLng? = null
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var geocoder: Geocoder

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startLocationUpdates()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                startLocationUpdates()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Location permission is required for map features.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        checkLocationPermission()

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val useCurrentLocationTextView = findViewById<TextView>(R.id.use_current_location_text_view)
        val locationResultsRecyclerView = findViewById<RecyclerView>(R.id.location_results_recycler_view)

        locationAdapter = LocationAdapter(emptyList()) { location ->
            navigateToHome(location)
        }
        locationResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        locationResultsRecyclerView.adapter = locationAdapter

        useCurrentLocationTextView.setOnClickListener {
            lastLocation?.let {
                try {
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        val location = Location(
                            name = address.featureName ?: "Unnamed Location",
                            address = address.getAddressLine(0),
                            latitude = address.latitude,
                            longitude = address.longitude
                        )
                        navigateToHome(location)
                    } else {
                        Toast.makeText(this, "Unable to determine location name", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(this, "Error getting location name", Toast.LENGTH_SHORT).show()
                }

            } ?: Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    searchLocations(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchLocations(query: String) {
        try {
            val addresses = geocoder.getFromLocationName(query, 5)
            if (addresses != null) {
                val locations = addresses.mapNotNull { address ->
                    if (address.featureName != null && address.getAddressLine(0) != null) {
                        Location(
                            name = address.featureName,
                            address = address.getAddressLine(0),
                            latitude = address.latitude,
                            longitude = address.longitude
                        )
                    } else {
                        null
                    }
                }
                locationAdapter.updateLocations(locations)
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Error searching for locations", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHome(location: Location) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NAVIGATE_TO", "HOME")
        intent.putExtra("location", location)
        startActivity(intent)
        finish()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                lastLocation = LatLng(it.latitude, it.longitude)
                Log.d("EnterLocationActivity", "Current location update: ${lastLocation?.latitude}, ${lastLocation?.longitude}")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        }
    }
}